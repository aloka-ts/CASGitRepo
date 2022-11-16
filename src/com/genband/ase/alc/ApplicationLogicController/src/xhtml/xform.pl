#!/usr/bin/perl -I. 
use POSIX qw(strftime);
use Getopt::Long;
use File::Find;
Getopt::Long::Configure("auto_abbrev");
GetOptions("alcmlPath:s", "xjcPath:s");

open(XSD_INPUT, "${opt_alcmlPath}/_Generated.xsd") or die "Cannot open master XSD file ${opt_alcmlPath}/_Generated.xsd\n";

my %typeDefs;

my $containerOutput;
my $itemOutput;
my %types;
while (<XSD_INPUT>)
{
      while (1)
      {
            if (/<xs:complexType name="([a-z,A-Z,0-9]+)type">/)
            {
                  my $container = 1;
                  my $name = $1;
                  my @attributeList;
                  my $inChoice = 0;
                  while (<XSD_INPUT>)
                  {
                        if (/<\/xs:complexType>/)
                        {
                              last;
                        }
                        if (/<xs:choice/)
                        {
                              $container = 2;
                        }
                        if (/xs:attribute\s+name="([a-z,A-Z,0-9]+)"/)
                        {     
                              if ($1 ne "Label")
                              {
                                    push @attributeList, $1;
                              }
                        }
                        if (/<xs:element\s+name="([a-z,A-Z,0-9]+)".*type="xs:string"/)
                        {
                              my $t = $types{$1};
                              if ($t)
                              {
                              }
                              else
                              {
                                    $itemOutput = $itemOutput."|".$1;
                                    $types{$1} = "item";
                              }
                        }
                        
                  }
            
                  $typeDefs{$name} = \@attributeList;
                  
                  if ($container == 2)
                  {
                        $containerOutput = $containerOutput."|".$name;
                        $types{$name} = "container";
                  }
                  else
                  {
                        $itemOutput = $itemOutput."|".$name;
                        $types{$name} = "item";
                  }
                  next;
            }
            last;
      }
}

close XSD_INPUT;


# OUTPUT

#Insert Node Descriptors transform
#####################################
open(IND_OUTPUT, ">InsertNodeDescriptors.xsl") or die;
print IND_OUTPUT "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".
"     <xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\" xmlns=\"\">\n".
"           <xsl:output method=\"xml\" indent=\"yes\"/>\n";

for $item (keys %typeDefs)
{
      my $type = $types{$item};
      if ($type eq "item")
      {
            print IND_OUTPUT "            <xsl:template match=\"//".$item."\">\n";
            print IND_OUTPUT "                  <xsl:element name=\"NodeDescriptor\">\n".
                              "                       <xsl:attribute name=\"action\">display</xsl:attribute>\n".
                              "                       <xsl:attribute name=\"display\">-</xsl:attribute>\n".
                              "                       <xsl:element name=\"ItemValue\">\n".
                              "                       <xsl:attribute name=\"displayText\">";
print IND_OUTPUT                                      $item;
my @attrs = @{$typeDefs{$item}};
for $attr (@attrs)
{
            print IND_OUTPUT "<xsl:if test=\"\@".$attr." != \'\'\">";
                                                      print IND_OUTPUT " ".$attr." "."<xsl:value-of select=\"\@".$attr."\"/>";
            print IND_OUTPUT "</xsl:if>";                                                 
}

            print IND_OUTPUT "</xsl:attribute>\n".
                              "                       </xsl:element>\n".
                        "                 </xsl:element>\n".
                              "                             <xsl:copy>\n".
                              "                                   <xsl:apply-templates select=\"*|text()|@*\"/>\n".
                              "                             </xsl:copy>\n".
                        "           </xsl:template>\n";
            
      }
      if ($type eq "container")
      {
            print IND_OUTPUT "            <xsl:template match=\"//".$item."\">\n";
            print IND_OUTPUT "                  <xsl:element name=\"NodeDescriptor\">\n".
                              "                       <xsl:attribute name=\"action\">display</xsl:attribute>\n".
                              "                       <xsl:attribute name=\"display\">+</xsl:attribute>\n".
                              "                       <xsl:attribute name=\"displayText\">".$item."</xsl:attribute>\n".                         
                              "                       <xsl:element name=\"Container\">\n".
                              "                       <xsl:attribute name=\"displayText\">";
print IND_OUTPUT                                      $item;
my @attrs = @{$typeDefs{$item}};
for $attr (@attrs)
{
            print IND_OUTPUT "<xsl:if test=\"\@".$attr." != \'\'\">";
                                                      print IND_OUTPUT " ".$attr." "."<xsl:value-of select=\"\@".$attr."\"/>";
            print IND_OUTPUT "</xsl:if>";                                                 
}

            print IND_OUTPUT "</xsl:attribute>\n".
                              "                       </xsl:element>\n".
                        "                 </xsl:element>\n".
                              "                             <xsl:copy>\n".
                              "                                   <xsl:apply-templates select=\"*|text()|@*\"/>\n".
                              "                             </xsl:copy>\n".
                        "           </xsl:template>\n";
      }

}


print IND_OUTPUT "            <xsl:template match=\"*|@*|text()\">\n".
"                 <xsl:copy>\n".
"                       <xsl:apply-templates select=\"*|text()|@*\"/>\n".
"                 </xsl:copy>\n".
"           </xsl:template>\n".
"</xsl:stylesheet>\n";

close IND_OUTPUT;

open(IND1_OUTPUT, ">RemoveContent.xsl") or die;
print IND1_OUTPUT "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".
"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\" xmlns=\"\">\n".
"           <xsl:output method=\"xml\" indent=\"yes\"/>\n";
            print IND1_OUTPUT "           <xsl:template match=\"//NodeDesciptor\">\n";
            print IND1_OUTPUT "                             <xsl:copy>\n".
                              "                                   <xsl:apply-templates select=\"*|text()|@*\"/>\n".
                              "                             </xsl:copy>\n".
                        "           </xsl:template>\n";



print IND1_OUTPUT "".
"</xsl:stylesheet>\n";
close IND1_OUTPUT;


#####################################

#XFORMS XHTML
#####################################
#slurp xhtml
open(XHTML_INPUT, "_xform.xhtml") or die;
my $xhtml = do { local( $/ ) ; <XHTML_INPUT> } ;
close XHTML_INPUT;

for $item (keys %typeDefs)
{
      my $type = $types{$item};
      if ($type eq "item")
      {
            #DISPLAY          
            #$xhtml =~ s/<!-- Preprocess ITEMS -->/\t\t\t\t\t<xf:group ref=\"$item\">\n\t\t\t\t\t\t<xf:output>\n\t\t\t\t\t\t\t<xf:label>$item: <\/xf:label>\n\t\t\t\t\t\t\t<\/xf:output>\n<!-- Preprocess ITEMS -->/;
            #my @attrs = @{$typeDefs{$item}};
            #for $attr (@attrs)
            #{
            #     $xhtml =~ s/<!-- Preprocess ITEMS -->/\t\t\t\t\t\t<xf:output ref=\"\@$attr\">\n\t\t\t\t\t\t\t<xf:label>$attr: <\/xf:label><\/xf:output>\n<!-- Preprocess ITEMS -->/;
            #}
            #$xhtml =~ s/<!-- Preprocess ITEMS -->/\t\t\t\t\t<\/xf:group>\n<!-- Preprocess ITEMS -->/;


            #EDIT
            $xhtml =~ s/<!-- Preprocess ITEM EDITS -->/\t\t\t\t\t<xf:group ref=\"$item\">\n\t\t\t\t\t\t<xf:output>\n\t\t\t\t\t\t\t<xf:label>$item: <\/xf:label>\n\t\t\t\t\t\t\t<\/xf:output>\n<!-- Preprocess ITEM EDITS -->/;
            my @attrs = @{$typeDefs{$item}};
            for $attr (@attrs)
            {
                  $xhtml =~ s/<!-- Preprocess ITEM EDITS -->/\t\t\t\t\t\t<xf:input ref=\"\@$attr\">\n\t\t\t\t\t\t\t<xf:label>$attr: <\/xf:label><\/xf:input>\n<!-- Preprocess ITEM EDITS -->/;
            }
            $xhtml =~ s/<!-- Preprocess ITEM EDITS -->/\t\t\t\t\t<\/xf:group>\n<!-- Preprocess ITEM EDITS -->/;
            
      }
      if ($type eq "container")
      {
#           #DISPLAY
#           $xhtml =~ s/<!-- Preprocess CONTAINERS -->/\t\t\t\t\t<xf:group ref=\"$item\">\n\t\t\t\t\t\t<xf:output>\n\t\t\t\t\t\t\t<xf:label>$item: <\/xf:label>\n\t\t\t\t\t\t\t<\/xf:output>\n<!-- Preprocess CONTAINERS -->/;
#           my @attrs = @{$typeDefs{$item}};
#           for $attr (@attrs)
#           {
#                 $xhtml =~ s/<!-- Preprocess CONTAINERS -->/\t\t\t\t\t\t<xf:output ref=\"\@$attr\">\n\t\t\t\t\t\t\t<xf:label>$attr: <\/xf:label><\/xf:output>\n<!-- Preprocess CONTAINERS -->/;
#           }
#           $xhtml =~ s/<!-- Preprocess CONTAINERS -->/\t\t\t\t\t<\/xf:group>\n<!-- Preprocess CONTAINERS -->/;
            
            #EDIT
            $xhtml =~ s/<!-- Preprocess CONTAINER EDITS -->/\t\t\t\t\t<xf:group ref=\"$item\">\n\t\t\t\t\t\t<xf:output>\n\t\t\t\t\t\t\t<xf:label>$item: <\/xf:label>\n\t\t\t\t\t\t\t<\/xf:output>\n<!-- Preprocess CONTAINER EDITS -->/;
            my @attrs = @{$typeDefs{$item}};
            for $attr (@attrs)
            {
                  $xhtml =~ s/<!-- Preprocess CONTAINER EDITS -->/\t\t\t\t\t\t<xf:input ref=\"\@$attr\">\n\t\t\t\t\t\t\t<xf:label>$attr: <\/xf:label><\/xf:input>\n<!-- Preprocess CONTAINER EDITS -->/;
            }
            $xhtml =~ s/<!-- Preprocess CONTAINER EDITS -->/\t\t\t\t\t<\/xf:group>\n<!-- Preprocess CONTAINER EDITS -->/;

      }
}

open(XHTML_OUTPUT, ">xform.xhtml") or die;
print XHTML_OUTPUT $xhtml;
close XHTML_OUTPUT;
#####################################



