import com.genband.ase.alc.alcml.jaxb.ALCMLActionClass;
import com.genband.ase.alc.alcml.jaxb.ALCMLActionMethod;
import com.genband.ase.alc.alcml.jaxb.ALCMLDefaults;
import com.genband.ase.alc.alcml.jaxb.ALCMLMethodParameter;


import com.sun.mirror.apt.*;
import com.sun.mirror.declaration.*;
import com.sun.mirror.type.*;
import com.sun.mirror.util.*;

import java.util.Collection;
import java.util.Set;
import java.util.Arrays;
import java.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.util.Collections.*;
import static com.sun.mirror.util.DeclarationVisitors.*;

/*
 */
public class GenerateALCMLxsdApFactory implements AnnotationProcessorFactory {
    // Process any set of annotations
    private static final Collection<String> supportedAnnotations
        = unmodifiableCollection(Arrays.asList("*"));

    // No supported options
    private static final Collection<String> supportedOptions = emptySet();

    public Collection<String> supportedAnnotationTypes() {
        return supportedAnnotations;
    }

    public Collection<String> supportedOptions() {
		/* this functions does nothing */
        return supportedOptions;
    }

    public AnnotationProcessor getProcessorFor(
            Set<AnnotationTypeDeclaration> atds,
            AnnotationProcessorEnvironment env) {
        return new GenerateALCMLxsdAp(env);
    }

    private static class GenerateALCMLxsdAp implements AnnotationProcessor {
		public String implPackage = null;
		public String implDir = null;
		public PrintStream xsdDeclFile;
		public PrintStream xsdTypeDeclFile;
		public boolean xsdDecl = true;
		public boolean xsdTypeDecl = true;
		public boolean javaImpl = true;
        private final AnnotationProcessorEnvironment env;
        GenerateALCMLxsdAp(AnnotationProcessorEnvironment env) {
            this.env = env;
        }

        public void process() {
			for (String key : env.getOptions().keySet())
			{
				{
					Pattern p = Pattern.compile("-AxsdDeclFile=(.*)");
					Matcher m = p.matcher(key);

					if (m.find())
					{
						try
						{
							xsdDeclFile = new PrintStream(new File(m.group(1)));
						}
						catch (Exception e)
						{
							System.out.println("failed creating "+m.group(1));
						}
					}
				}
				{
					Pattern p = Pattern.compile("-AxsdTypeDeclFile=(.*)");
					Matcher m = p.matcher(key);

					if (m.find())
					{
						try
						{
							xsdTypeDeclFile = new PrintStream(new File(m.group(1)));
						}
						catch (Exception e)
						{
							System.out.println("failed creating "+m.group(1));
						}
					}
				}
				{
					Pattern p = Pattern.compile("-AimplDir=(.*)");
					Matcher m = p.matcher(key);

					if (m.find())
					{
						implDir = m.group(1);
					}
				}
			}
			try
			{
				for (TypeDeclaration typeDecl : env.getSpecifiedTypeDeclarations())
					typeDecl.accept(getDeclarationScanner(new GenerateXSDVisitor(this), NO_OP));

				xsdDeclFile.close();
				xsdTypeDeclFile.close();
			}
			catch (Exception e)
			{
				System.out.println(e);
			}
        }

        private static class GenerateXSDVisitor extends SimpleDeclarationVisitor {
			GenerateXSDVisitor(GenerateALCMLxsdAp processor)
			{
				this.processor = processor;
			}
            public void visitClassDeclaration(ClassDeclaration d) {
				ALCMLActionClass a = (ALCMLActionClass)d.getAnnotation(ALCMLActionClass.class);
				if (a!=null)
				{

					PrintStream xsdTypeDeclarationOut = new PrintStream(xsdTypeDeclaration, true);

					if (!a.literalXSDDefinition().equals(ALCMLDefaults.Unimplemented))
						xsdTypeDeclarationOut.printf("%s",a.literalXSDDefinition());

					for (MethodDeclaration methDecl : d.getMethods())
					{
						ALCMLActionMethod am = (ALCMLActionMethod)methDecl.getAnnotation(ALCMLActionMethod.class);
						if (am!=null)
						{
							String sFileName = new String(processor.implDir + "/");
							String methodPreamble = new String("");
							String methodCall = new String("");
							String methodEpilogue = new String("");
							int listcounter=0;
							PrintStream javaImplOut = new PrintStream(javaImplBuff, true);
							PrintStream xsdListAdditionOut = new PrintStream(xsdListAddition, true);
							PrintStream elementDeclarationOut = new PrintStream(elementDeclaration, true);
							PrintStream attributeDeclarationOut = new PrintStream(attributeDeclaration, true);

							javaImplOut.printf("/**************************************************************************/\n/** This is a generated class by alcmlc compiler                         **/\n/** It extends the Genband ALCML implementation and provides             **/\n/** bindings to the ALC process engine                                   **/\n/** Copyright 2007 Genband, Inc. All Rights Reserved.                    **/\n/**************************************************************************/\n");
							javaImplOut.printf("import %s;\n", d.getQualifiedName());
							javaImplOut.printf("import com.genband.ase.alc.alcml.jaxb.*;\n");
							javaImplOut.printf("import com.genband.ase.alc.alcml.jaxb.xjc.*;\n");
							javaImplOut.printf("import com.genband.ase.alc.alcml.ALCServiceInterface.*;\n");
							javaImplOut.printf("import java.util.List;\n");
							javaImplOut.printf("import java.util.LinkedList;\n");
							javaImplOut.printf("import java.util.Iterator;\n");
							javaImplOut.printf("import java.lang.*;\n");
							javaImplOut.printf("import java.math.*;\n");

							elementDeclarationOut.println("\t\t<xs:sequence>");

							String ALCMLActionMethodName = am.name();
							if (ALCMLActionMethodName.equals(ALCMLDefaults.JavaSourceName))
								ALCMLActionMethodName = methDecl.getSimpleName();

							String XSDDataType = ALCMLActionMethodName + "type";

							XSDDataType = XSDDataType.replaceFirst(String.valueOf(XSDDataType.charAt(0)), String.valueOf(XSDDataType.charAt(0)).toUpperCase());

							while (XSDDataType.indexOf('-') != -1)
							{
								int firstOcc = XSDDataType.indexOf('-') ;
								String upperFirstLetter = String.valueOf(XSDDataType.charAt(firstOcc+1));
								String origString = "-" + upperFirstLetter;
								XSDDataType = XSDDataType.replace(origString, upperFirstLetter.toUpperCase());
							}

							if (processor.javaImpl)
							{
								sFileName = sFileName + XSDDataType + "ServiceUserAction" ;
								javaImplOut.printf("public class %sServiceUserAction extends ServiceUserAction \n{\n", XSDDataType);
								javaImplOut.printf("\tpublic String Display()\n\t{\n\t\tString display = new String(\"<%s \");\n\t\t", ALCMLActionMethodName);
								{
									for (ParameterDeclaration parmDecl : methDecl.getParameters())
									{
										ALCMLMethodParameter mp = (ALCMLMethodParameter)parmDecl.getAnnotation(ALCMLMethodParameter.class);
										if (mp != null)
										{
											if (mp.asAttribute())
											{
												String parmName = mp.name();
												if (parmName.equals(ALCMLDefaults.JavaSourceName))
													parmName = parmDecl.getSimpleName();


												String getter = "get";
												if (mp.type().equals(ALCMLDefaults.XSDBoolean))
													getter = "is";

												String JAXBReformat = parmName.replaceFirst(String.valueOf(parmName.charAt(0)), String.valueOf(parmName.charAt(0)).toUpperCase());

												while (JAXBReformat.indexOf('-') != -1)
												{
													int firstOcc = JAXBReformat.indexOf('-') ;
													String upperFirstLetter = String.valueOf(JAXBReformat.charAt(firstOcc+1));
													String origString = "-" + upperFirstLetter;
													JAXBReformat = JAXBReformat.replace(origString, upperFirstLetter.toUpperCase());
												}

												getter = getter + JAXBReformat;

												if (!mp.type().equals(ALCMLDefaults.XSDBoolean))
													javaImplOut.printf("if (((%s)myAction).%s() != null)", XSDDataType, getter);
												javaImplOut.printf(" display += \" %s=\\\"\" + ((%s)myAction).%s() + \"\\\"\";\n\t\t", parmName, XSDDataType, getter);
											}
										}
									}
									javaImplOut.printf("return display;\n\t}\n");
								}
								javaImplOut.printf("\n\tpublic String EndOfBlockDisplay()\n\t{\n\t\treturn \"</%s>  <!--\" + Display() +\"-->\"; \n\t}\n", ALCMLActionMethodName);
								javaImplOut.printf("\n\tpublic String getServiceMethod()\n\t{\n\t\treturn \"%s\";\n\t}\n", ALCMLActionMethodName);
								javaImplOut.printf("\n\tpublic String getServiceName()\n\t{\n\t\treturn \"%s\";\n\t}\n", d.getSimpleName());
								javaImplOut.printf("\n\tpublic void ExecuteUserFunction(ServiceContext context) throws ServiceActionExecutionException\n");
								javaImplOut.printf("\t{\n");
								boolean staticMethod = false;
								for (Modifier mod : methDecl.getModifiers())
								{
									if (mod.equals(Modifier.STATIC))
										staticMethod = true;
								}
								if (staticMethod)
								{
									methodCall += "\t\t"+d.getQualifiedName()+"."+methDecl.getSimpleName()+"(";
									if (((ParameterDeclaration)methDecl.getParameters().toArray()[0]).getType().toString().equals("ServiceContext") || ((ParameterDeclaration)methDecl.getParameters().toArray()[0]).getType().toString().equals("com.genband.ase.alc.alcml.jaxb.ServiceContext"))
									{
										methodCall += "context";
									}
									else
									{
										methodEpilogue += "\n\t\tcontext.ActionCompleted();\n";
									}
								}
								else
								{
									javaImplOut.printf("\t\t%s userDef = ((%s)context.getSBBImplementors().get(\"%s\"));\n", d.getQualifiedName(), d.getQualifiedName(), d.getSimpleName());
									javaImplOut.printf("\t\tif (userDef == null)\n");
									javaImplOut.printf("\t\t{\n");
									javaImplOut.printf("\t\t\tuserDef = new %s();\n", d.getQualifiedName());
									javaImplOut.printf("\t\t\tif (myDef.getServiceContextProvider() != null)\n");
									javaImplOut.printf("\t\t\t\t((ALCServiceInterface)userDef).Initialize(myDef.getServiceContextProvider());\n");
									javaImplOut.printf("\t\t\tcontext.setServiceBlock(\"%s\", userDef);\n", d.getSimpleName());
									javaImplOut.printf("\t\t}\n");

									javaImplOut.printf("\t\tALCServiceInterface intf = (ALCServiceInterface)userDef;\n");
									javaImplOut.printf("\t\tcontext.addToContext(intf.getContext());\n");

									methodCall += "\t\tuserDef." + methDecl.getSimpleName() + "(context";
								}

							}


							xsdTypeDeclarationOut.printf("\t<xs:complexType name=\"%stype\">\n", ALCMLActionMethodName);
							if (!am.help().equals(ALCMLDefaults.NoHelpAvailable))
								xsdTypeDeclarationOut.printf("\t\t<xs:annotation>\n\t\t\t<xs:documentation>%s</xs:documentation>\n", am.help().replaceAll("\n", "  ").replaceAll("'", "&#44;"));
							else
								xsdTypeDeclarationOut.printf("\t\t<xs:annotation>\n");

							xsdTypeDeclarationOut.printf("\t\t\t<xs:appinfo>\n\t\t\t\t<source>%s</source>\n\t\t\t</xs:appinfo>\n\t\t</xs:annotation>\n", a.name());


							xsdListAdditionOut.printf("\t\t\t<xs:element name=\"%s\" maxOccurs=\"unbounded\" type=\"%stype\" minOccurs=\"0\">\n", ALCMLActionMethodName, ALCMLActionMethodName);
							if (!am.help().equals(ALCMLDefaults.NoHelpAvailable))
								xsdListAdditionOut.printf("\t\t\t\t<xs:annotation>\n\t\t\t\t\t<xs:documentation>%s</xs:documentation>\n\t\t\t\t</xs:annotation>\n", am.help().replaceAll("\n", "  ").replaceAll("'", "&#44;"));
							xsdListAdditionOut.printf("\t\t\t</xs:element>\n");

							for (ParameterDeclaration parmDecl0 : methDecl.getParameters())
							{
								if (parmDecl0.getType().toString().equals("ServiceContext") || parmDecl0.getType().toString().equals("com.genband.ase.alc.alcml.jaxb.ServiceContext"))
									break;
								processor.xsdDeclFile.println("error: First parameter in ALCML declaration for method must be of type ServiceContext");
								processor.xsdDeclFile.println(methDecl);
								processor.xsdDeclFile.println("** " + parmDecl0.getType() + " **");
								break;
							}
							for (ParameterDeclaration parmDecl : methDecl.getParameters())
							{
								ALCMLMethodParameter mp = (ALCMLMethodParameter)parmDecl.getAnnotation(ALCMLMethodParameter.class);
								if (mp != null)
								{
									String parmName = mp.name();
									boolean isAListOfUserDefinedStuff = false;
									boolean isAListofStrings = false;

									if (parmName.equals(ALCMLDefaults.JavaSourceName))
										parmName = parmDecl.getSimpleName();

									if (parmDecl.getType().toString().contains("java.util.List"))
									{
										isAListOfUserDefinedStuff = true;
									}

									if (parmDecl.getType().toString().equals("java.util.List<java.lang.String>")|| parmDecl.getType().toString().equals("List<String>"))
									{
										isAListOfUserDefinedStuff = false;
										isAListofStrings = true;
									}

									if (isAListofStrings)
									{
										if (!mp.help().equals(ALCMLDefaults.NoHelpAvailable))
											elementDeclarationOut.printf("\t\t\t<!-- %s -->\n", mp.help().replaceAll("\n", "  ").replaceAll("'", "&#44;"));

										elementDeclarationOut.printf("\t\t\t<xs:element name=\"%s\" type=\"listtype\"/>\n", parmName);
									}
									else
									{
										if (mp.asAttribute())
										{

											attributeDeclarationOut.printf("\t\t<xs:attribute name=\"%s\" type=\"%s\"", parmName, mp.type());
											if (!mp.defaultValue().equals(ALCMLDefaults.NoDefaultAvailable))
											{
												attributeDeclarationOut.printf(" default=\"%s\"", mp.defaultValue());
											}
											if (mp.required())
											{
												attributeDeclarationOut.printf(" use=\"required\"");
											}
											else
											{
												attributeDeclarationOut.printf(" use=\"optional\"");
											}
											attributeDeclarationOut.printf(">\n");
											if (!mp.help().equals(ALCMLDefaults.NoHelpAvailable))
												attributeDeclarationOut.printf("\t\t\t<xs:annotation>\n\t\t\t\t<xs:documentation>%s</xs:documentation>\n\t\t\t</xs:annotation>\n", mp.help().replaceAll("\n", "  ").replaceAll("'", "&#44;"));
											attributeDeclarationOut.printf("\t\t</xs:attribute>\n");
										}
										else
										{
											elementDeclarationOut.printf("\t\t\t<xs:element name=\"%s\" type=\"%s\"", parmName, mp.type());
											if (mp.required())
											{
												elementDeclarationOut.printf(" minOccurs=\"1\" maxOccurs=\"1\"");
											}
											else
											{
												elementDeclarationOut.printf(" minOccurs=\"0\" maxOccurs=\"1\"");
											}
											elementDeclarationOut.printf(">\n");
											if (!mp.help().equals(ALCMLDefaults.NoHelpAvailable))
												elementDeclarationOut.printf("\t\t\t\t<xs:annotation>\n\t\t\t\t\t<xs:documentation>%s</xs:documentation>\n\t\t\t\t</xs:annotation>\n", mp.help().replaceAll("\n", "  ").replaceAll("'", "&#44;"));
											elementDeclarationOut.printf("\t\t\t</xs:element>\n");
										}
									}

									if (processor.javaImpl)
									{
										String getter = "get";
										if (mp.type().equals(ALCMLDefaults.XSDBoolean))
											getter = "is";

										String JAXBReformat = parmName.replaceFirst(String.valueOf(parmName.charAt(0)), String.valueOf(parmName.charAt(0)).toUpperCase());

										while (JAXBReformat.indexOf('-') != -1)
										{
											int firstOcc = JAXBReformat.indexOf('-') ;
											String upperFirstLetter = String.valueOf(JAXBReformat.charAt(firstOcc+1));
											String origString = "-" + upperFirstLetter;
											JAXBReformat = JAXBReformat.replace(origString, upperFirstLetter.toUpperCase());
										}

										getter = getter + JAXBReformat;

										if (isAListofStrings)
										{
											String ListReplacement = new String("");

											listcounter++;
											ListReplacement += "\t\tLinkedList<String> afterReplacementList" + listcounter + " = new LinkedList<String>();\n";
											ListReplacement += "\t\t{\n";
											ListReplacement += "\t\t\tIterator listValues = ((" + XSDDataType + ")myAction)." + getter + "().getItem().iterator();\n" ;
											ListReplacement += "\t\t\twhile (listValues.hasNext())\n";
											ListReplacement += "\t\t\t{\n";
											ListReplacement += "\t\t\t\tString stringValue = (String)listValues.next();\n";
											ListReplacement += "\t\t\t\tString sValue = ALCMLExpression.toString(context, stringValue);\n";
											ListReplacement += "\t\t\t\tafterReplacementList" + listcounter + ".add(sValue);\n";
											ListReplacement += "\t\t\t}\n";
											ListReplacement += "\t\t}\n";

											methodCall += ", afterReplacementList" + listcounter;

											javaImplOut.printf(ListReplacement);
										}
										else
										{
											if (mp.type().equals(ALCMLDefaults.ALCMLExpression))
											{
												String castedGetterName = "(("+XSDDataType+")myAction)."+getter+"()";
												if (parmDecl.getType().toString().equals(String.class.getName()))
													methodCall += ",\n\t\t\tALCMLExpression.toString(context, "+ castedGetterName+")";
												else
												if (parmDecl.getType().toString().equals(Integer.class.getName()))
													methodCall += ",\n\t\t\tALCMLExpression.toInteger(context, "+ castedGetterName+")";
												else
												{
													methodPreamble += "\t\tALCMLExpression _"+getter+" = null;\n\t\tif (" +castedGetterName+" != null)\n\t\t\t_"+getter+" = new ALCMLExpression(context, "+ castedGetterName+");\n";
													methodCall += ",\n\t\t\t_"+getter;
												}

											}
											else
												methodCall += ",\n\t\t\t(("+XSDDataType+")myAction)."+getter+"()";
										}
									}
								}
							}
							if (processor.javaImpl)
							{
								javaImplOut.printf(methodPreamble);
								javaImplOut.printf(methodCall);
								javaImplOut.printf(methodEpilogue);
								javaImplOut.printf(");\n\t}\n");
								javaImplOut.printf("\n\tpublic boolean isAnAtomicAction()\n\t{\n\t\treturn %s;\n\t}\n", am.isAtomic());
								javaImplOut.printf("\n\tpublic List<Resultstype> getResults()\n\t{\n\t\treturn ((%s)myAction).getResults();\n\t}\n", XSDDataType);
								javaImplOut.printf("\n\tpublic DefaultActiontype getDefaultAction()\n\t{\n\t\treturn ((%s)myAction).getDefaultAction();\n\t}\n", XSDDataType);
								javaImplOut.printf("\n\tpublic Boolean isAsynch()\n\t{\n\t\treturn ((%s)myAction).isAsynch();\n\t}\n", XSDDataType);
								javaImplOut.printf("\n\tpublic String getNextActionLabel()\n\t{\n\t\treturn ((%s)myAction).getNextAction();\n\t}\n", XSDDataType);
								javaImplOut.printf("\n\tpublic String getMethod()\n\t{\n\t\treturn new String(\"%s\");\n\t}\n", ALCMLActionMethodName);
								javaImplOut.printf("\n\tpublic String getLabel()\n\t{\n\t\treturn ((%s)myAction).getLabel();\n\t}\n", XSDDataType);
								javaImplOut.printf("\n\tstatic\n\t{\n\t\tServiceDefinition.RegisterCreationClass(%s.class.getName(), %sServiceUserAction.class);\n\t}\n", XSDDataType, XSDDataType);
								javaImplOut.printf("}\n");
							}

							if (processor.javaImpl)
							{
								try
								{
									FileWriter fw = new FileWriter(sFileName + ".java");
									fw.write(javaImplBuff.toString());
									fw.flush();
								}
								catch (IOException e)
								{
									System.out.println("APT::GenerateALCMLxsdApFactory Can't write file " + sFileName);
									System.out.println(e);
									return;
								}
							}
							javaImplBuff = new ByteArrayOutputStream();

							if (am.canHaveResults())
								elementDeclarationOut.printf("\t\t\t<xs:element name=\"results\" maxOccurs=\"unbounded\" type=\"resultstype\" minOccurs=\"0\"/>\n");
							elementDeclarationOut.printf("\t\t\t<xs:element name=\"next-action\" minOccurs=\"0\" type=\"xs:string\"/>\n");
							if (am.canHaveResults())
								elementDeclarationOut.printf("\t\t\t<xs:element name=\"default-action\" type=\"default-actiontype\" minOccurs=\"0\" maxOccurs=\"1\"/>\n");

							elementDeclarationOut.println("\t\t</xs:sequence>");

							attributeDeclarationOut.printf("\t\t<xs:attribute name=\"label\" type=\"xs:string\" use=\"optional\"/>\n");
							attributeDeclarationOut.printf("\t\t<xs:attribute name=\"asynch\" type=\"xs:boolean\" use=\"optional\"/>\n");

							xsdTypeDeclarationOut.printf(elementDeclaration.toString());
							elementDeclaration = new ByteArrayOutputStream();
							xsdTypeDeclarationOut.printf(attributeDeclaration.toString());
							attributeDeclaration = new ByteArrayOutputStream();

							xsdTypeDeclarationOut.printf("\t</xs:complexType>\n");

						}
					}
					processor.xsdTypeDeclFile.println("\t<!-- " + a.name() + " - " + a.help() + " -->");
					processor.xsdTypeDeclFile.println(xsdTypeDeclaration.toString());
					xsdTypeDeclaration = new ByteArrayOutputStream();
					processor.xsdDeclFile.println("\t\t\t<!-- " + a.name() + " - " + a.help() + " -->");
					processor.xsdDeclFile.println(xsdListAddition.toString());
					xsdListAddition = new ByteArrayOutputStream();
				}
            }
			GenerateALCMLxsdAp processor;
			ByteArrayOutputStream javaImplBuff = new ByteArrayOutputStream();

			ByteArrayOutputStream xsdTypeDeclaration = new ByteArrayOutputStream();
			ByteArrayOutputStream xsdListAddition = new ByteArrayOutputStream();
			ByteArrayOutputStream elementDeclaration = new ByteArrayOutputStream();
			ByteArrayOutputStream attributeDeclaration = new ByteArrayOutputStream();
            PrintStream out;

        }
    }
}

