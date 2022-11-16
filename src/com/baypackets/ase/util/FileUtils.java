/*
 * FileUtils.java
 *
 * Created on August 7, 2004, 2:57 PM.
 */
package com.baypackets.ase.util;

import com.baypackets.ase.util.exceptions.FileCopyException;
import com.baypackets.ase.util.exceptions.FileMoveException;
import com.baypackets.ase.util.exceptions.FileDeleteException;
import com.baypackets.ase.util.exceptions.ZipExtractException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.util.regex.Pattern;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.log4j.Logger;



/**
 * This class provides utility methods for working with files.
 *
 * @author  Zoltan Medveczky
 */
public final class FileUtils {

    private static Logger _logger = Logger.getLogger(FileUtils.class);

    private static int BUFFER_SIZE = 1000;


    /**
     * Private constructor.
     */
    private FileUtils() {
    }


    /**
     * Executes a sample app to test the methods defined in this class.
     *
     * @param args contains the name of the method to execute and any required
     * parameters
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
				if (_logger.isInfoEnabled())
                _logger.info("Usage: java FileUtils [methodName] [param1] ... [paramN]");
                System.exit(1);
            }

            String methodName = args[0].trim().toLowerCase();

            if (methodName.equals("extract")) {
                ZipInputStream zipStream = new ZipInputStream(new FileInputStream(args[1]));
                extract(zipStream, new File(args[2]));
            } else if (methodName.equals("copy")) {
                copy(new File(args[1]), new File(args[2]));
            } else if (methodName.equals("delete")) {
                delete(new File(args[1]));
            } else if (methodName.equals("move")) {
                move(new File(args[1]), new File(args[2]));
            } else {
                _logger.info("invalid method: " + methodName);
                System.exit(1);
            }
        } catch (Exception e) {
	    _logger.error(e.getMessage(), e);
        }
    }


    /**
     * This method extracts the contents of the given zipped input stream to
     * the specified directory.
     *
     * @param zipStream  A zipped input stream to extract
     * @param destDir  The directory to extract the zipped contents to
     * @return flag  Indicates whether any content was extracted to the
     * specified directory
     */
    public static boolean extract(ZipInputStream zipStream, File destDir) throws ZipExtractException {
        try {
            boolean extracted = false;
			if (_logger.isDebugEnabled()) {
			_logger.debug("TESTSURESH: File = "+destDir);
			_logger.debug("TESTSURESH: stream = "+zipStream);
			}
			try {
				if (_logger.isDebugEnabled())
				_logger.debug("TESTSURESH: available = "+zipStream.available());
				ZipEntry entry = zipStream.getNextEntry();
				if (_logger.isDebugEnabled())
				_logger.debug("TESTSURESH:  ZipEntry = "+entry);

			} catch (Throwable th) {
				if (_logger.isDebugEnabled())
				_logger.debug("Throwable = "+th);
			}

            for (ZipEntry entry = zipStream.getNextEntry(); entry != null; entry = zipStream.getNextEntry()) {
                File file = new File(destDir, entry.getName());

                if (file.isDirectory() && file.exists()) {
                    continue;
                }

                extracted = true;

                if (entry.isDirectory()) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("creating directory: " + file.getAbsolutePath());
                    }
                    file.mkdirs();
                } else {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("creating file: " + file.getAbsolutePath());
                    }

                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }
					if (file.exists())
						file.delete();

                    file.createNewFile();

                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), BUFFER_SIZE);

                    byte[] bytes = new byte[BUFFER_SIZE];

                    int bytesRead = zipStream.read(bytes, 0, bytes.length);

                    while (bytesRead > 0) {
                        bos.write(bytes, 0, bytesRead);
                        bytesRead = zipStream.read(bytes, 0, bytes.length);
                    }

                    bos.close();
                }
            }

            zipStream.close();

            return extracted;
        } catch (Exception e) {
			_logger.error("Unable to extract",e);
            throw new ZipExtractException(e.toString());
        }
    }

    public static boolean decompress(TarArchiveInputStream tarStream, File destDir) throws IOException {
		 boolean extracted = false;
	        try{
	            TarArchiveEntry entry;
	            while ((entry = tarStream.getNextTarEntry()) != null) {
	                if (entry.isDirectory()) {
	                    continue;
	                }
	                File curfile = new File(destDir, entry.getName());
	                File parent = curfile.getParentFile();
	                if (!parent.exists()) {
	                    parent.mkdirs();
	                }
	                
	                IOUtils.copy(tarStream, new FileOutputStream(curfile));
	                
	                extracted = true;
	            }
	            
	            tarStream.close();
	        }catch(IOException ex){
	        	_logger.error("Unable to Untar");
	        	throw new IOException();
	        }
	        
	        return extracted;
	    }

    /**
     * Removes the file referenced by the given File object.  If the File
     * object refers to a directory, this function will recursively delete the
     * contents of that directory before removing it.
     */
    public static void delete(File file) throws FileDeleteException {
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                for (int i = 0; i < files.length; i++) {
                    delete(files[i]);
                }
            }
            file.delete();
        } catch (Exception e) {
            throw new FileDeleteException(e.toString());
        }
    }


    /**
     * Empties the contents of the specified directory.
     */
    public static void empty(File dir) throws FileDeleteException {
        if (dir == null || !dir.isDirectory()) {
            return;
        }
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            delete(files[i]);
        }
    }


    /**
     * Returns the file extension of the given File or returns null if it
     * has none.
     */
    public static String getExtension(File file) {
        String fileName = file.getName();
        int index = fileName.lastIndexOf(AseStrings.PERIOD);
        if (index < 0) {
            return null;
        }
        return fileName.substring(index + 1, fileName.length());
    }


    /**
     * Copies the file or directory referenced by the given File object to
     * the specified location.  A File object representing the copied file
     * or directory is returned.
     */
    public static File copy(File file, File destDir) throws FileCopyException {
        try {
            return copy(file, destDir, false);
        } catch (Exception e) {
            throw new FileCopyException(e.toString());
        }
    }


    /**
     * Moves the file or directory referenced by the given File object to the
     * specified location.  A File object representing the moved file or
     * directory is returned.
     */
    public static File move(File file, File destDir) throws FileMoveException {
        try {
            return copy(file, destDir, true);
        } catch (Exception e) {
            throw new FileMoveException(e.toString());
        }
    }


    /**
     * Copies the file or directory referenced by the given File object to the
     * specified location.  If the value of the given "delete" flag is true,
     * all files and directories will be removed from their original location
     * once they have been copied to the destination directory.
     */
    private static File copy(File file, File destDir, boolean delete) throws Exception {
        File newFile = new File(destDir, file.getName());

        if (file.isDirectory()) {
            newFile.mkdir();

            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                copy(files[i], newFile);
            }
        } else {
            newFile.createNewFile();

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(newFile), BUFFER_SIZE);

            byte[] bytes = new byte[BUFFER_SIZE];

            int bytesRead = bis.read(bytes, 0, bytes.length);

            while (bytesRead > 0) {
                bos.write(bytes, 0, bytesRead);
                bytesRead = bis.read(bytes, 0, bytes.length);
            }

            bis.close();
            bos.close();
        }

        if (delete) {
            delete(file);
        }

        return newFile;
    }


	/**
	 * Searches the specified directory for all files whose name matches the
	 * given regular expression.
	 *
	 * @param dir  The directory in which to begin the search.
	 * @param pattern   The regular expression to match against the names of
	 * all files encountered.
	 * @param recurse  Indicates whether the specified directory should be
	 * searched recursivley.
	 * @return  The list of files found.
	 */
	public static File[] findFiles(File dir, Pattern pattern, boolean recurse) {
		return (File[])findFiles(dir, pattern, recurse, new ArrayList()).toArray(new File[0]);
	}

	private static Collection findFiles(File dir, Pattern pattern, boolean recurse, Collection matches) {
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory() && recurse) {
				findFiles(files[i], pattern, recurse, matches);
			}
			if (pattern.matcher(files[i].getName()).matches()) {
				matches.add(files[i]);
			}
		}
		return matches;
	}


	/**
	 * Searches the specified list of directories for all files whose name
	 * matches the given regular expression.
	 *
	 * @param dirs  The list of directories to be searched.
	 * @param pattern  The regular expression to be matched against the names
	 * of all files encountered.
	 * @param recurse  Indicates whether the directories should be searched
	 * recursively.
	 * @return  The list of found files.
	 */
	public static File[] findFiles(File[] dirs, Pattern pattern, boolean recurse) {
		Collection files = new ArrayList();

		for (int i = 0; i < dirs.length; i++) {
			files.addAll(Arrays.asList(findFiles(dirs[i], pattern, recurse)));
		}
		return (File[])files.toArray(new File[files.size()]);
	}


	/**
	 * Searches the specified list of directories for all files whose name
	 * matches the given regular expression.
	 *
	 * @param dirs  A delimited list of directories to be searched.
	 * @param delimiter  The character delimiting the directory names.
	 * @param pattern  The regular expression to match against the names of all
	 * files encountered.
	 * @param recurse  Indicates whether the directories should be search
	 * recursively.
	 * @return  The list of found files.
	 */
	public static File[] findFiles(String dirs, String delimiter, Pattern pattern, boolean recurse) {
		StringTokenizer tokens = new StringTokenizer(dirs, delimiter);

		File[] files = new File[tokens.countTokens()];

		for (int i = 0; i < files.length; i++) {
			files[i] = new File(tokens.nextToken());
		}
		return findFiles(files, pattern, recurse);
	}


	/**
	 * Searches the indicated sub-directories of the specified root directory for
	 * all files whose name matches the given regular expression.
	 *
	 * @param rootDir  The root directory from which to begin the search.
	 * @param subDirs  The names of the sub-directories in which to search.
	 * @param delimiter  The character used to delimit the sub-directory names
	 * in "subDirs".
	 * @param pattern  The regular expression to match against all encountered
	 * file names.
	 * @param recurse  Indicates whether the sub-directories should be searched
	 * recursively.
	 * @return  The list of found files.
	 */
	public static File[] findFiles(File rootDir, String subDirs, String delimiter, Pattern pattern, boolean recurse) {
		Collection files = new ArrayList(3);
		StringTokenizer tokens = subDirs != null ? new StringTokenizer(subDirs, delimiter) : null;

		if (tokens == null || !tokens.hasMoreTokens()) {
			files.addAll(Arrays.asList(findFiles(rootDir, pattern, recurse)));
		} else {
			while (tokens.hasMoreTokens()) {
				files.addAll(Arrays.asList(findFiles(new File(rootDir, tokens.nextToken()), pattern, recurse)));
			}
		}
		return (File[])files.toArray(new File[files.size()]);
	}


	/**
	 * Converts the given File objects to a list of URLs.
	 */
	public static Collection toUrls(File[] files) {
		try {
			Collection urls = new ArrayList(files.length);

			for (int i = 0; i < files.length; i++) {
				urls.add(files[i].toURL());
			}
			return urls;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}


	public static File createJar(String jarName, File jarDir, File[] files){
		return createJar(jarName,jarDir, null, files);
	}

	public static File createJar(String jarName, File jarDir) {
		try {
			File[] files = getAllFiles(jarDir);
			File targetFile = new File(jarDir.toString()+"/..");
			return createJar(jarName, targetFile,null,files);
		} catch(Exception e) {
			_logger.error(e.getMessage(),e);
		}
		return null;
	
	}
			
	public static File[] getAllFiles(File dir) {
		File[] files = null;
		try {
			List<File> list = getFileListing(dir);
			files = new File[list.size()];
			int count = 0;
			for(int i= 0; i<list.size(); i++) {
				File file = (File)list.get(i);
				if(!file.isDirectory()) {
					files[count] = file;
					count++;
				}
			}
		} catch(Exception e) {
			_logger.error(e.getMessage(),e);
		}
		return files;
	}

	public static File createJar(String jarName, File jarDir, File manifestFile, File[] files){
		Manifest manifest = null;
		JarOutputStream jarOut = null;
		File returnFile = null;
		Properties jarManifestProperties = null;
		try {
			if(manifestFile==null || !manifestFile.exists()){
				if(_logger.isDebugEnabled()) {
					_logger.debug("Manifest file is null or does not exist!");
					_logger.debug("Creating new manifest file...");
				}
				manifest = new Manifest();
			} else {
				InputStream manifestIn = new FileInputStream(manifestFile);
				jarManifestProperties = new Properties();
				jarManifestProperties.load(manifestIn);
				String mainClassName = jarManifestProperties.getProperty("Main-Class");
				if(_logger.isDebugEnabled()) {
					_logger.debug("Manifest Main-Class: "+mainClassName);
				}
				manifest = new Manifest();
				manifest.getMainAttributes().putValue(
								"Main-Class", mainClassName);
			}
																	       
			//Create the file with output stream (and manifest)
			File jarFile = new File(jarDir, jarName);
			OutputStream fileOut = new FileOutputStream(jarFile);
			if(manifest == null) {
				jarOut = new JarOutputStream(fileOut);
			} else {
				jarOut = new JarOutputStream(fileOut, manifest);
			}

			for(File file: files){
				writeToJar(file, jarOut);
			}
			jarOut.flush();
			jarOut.close();
			returnFile = jarFile;
		} catch(Exception e) {
			_logger.error("Unable to create jar file",e);
			return null;
		}
		return returnFile;
	}

	private static byte[] getFileBytes(File file) throws IOException, FileNotFoundException{
		long fileSize = file.length();
		byte[] arr = new byte[(int)fileSize];
		FileInputStream fileIn = new FileInputStream(file);
		fileIn.read(arr);
		return arr;
	}

	private static void writeToJar(File file, JarOutputStream jarOut) throws IOException, FileNotFoundException{
		byte[] fileBytes = getFileBytes(file);
		String fileName = file.getName();//RELATIVE NAME>>>>>
		if(_logger.isDebugEnabled()) {
			_logger.debug("Adding file..."+fileName);
		}

		//fileName = fileName.replaceAll("\\", "/"); (???)
		JarEntry entry = new JarEntry(file.getPath());
		jarOut.putNextEntry(entry);
		jarOut.write(fileBytes);
		jarOut.flush();
		if(file.isDirectory()){
			for(File child: file.listFiles()){
				writeToJar(child, jarOut);
			}
		}
	}


	private static List<File> getFileListing( File aStartingDir) throws FileNotFoundException {
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for(File file : filesDirs) {
			result.add(file); //always add, even if directory
			if ( ! file.isFile() ) {
				//must be a directory
				//recursive call!
				List<File> deeperList = getFileListing(file);
				result.addAll(deeperList);
			}
		}
		return result;
	}

						   

}

