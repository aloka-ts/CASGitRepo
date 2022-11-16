#!/usr/bin/perl

#------------------------------------------------------#
# Non EMS Installer for CAS.
# Version 1.0
# Nasir 11/30/04
# Bugs :
#------------------------------------------------------#

use Config;
my $length;
my @files ;
my $file;
my $newfile;
my %macro_hash = ();  # map to store macro values
my %param_hash = ();  # map to store param values

my $key;       	# temporary key
my $value;     	# temporary value
my $question;  	# question string
my $default;    # default value string
my $parsing=0; 	# 0 not parsing, 1 prsng MACRO, 2 prsng EQUALITY, 3 FILES

my $user = getlogin();

########## This is just to print date and month in log file

my $completedate = localtime();
($day, $month, $date, $time,) = split (/\s/, $completedate);
my $fulldate = $user."_".$day."_".$month."_".$date;

###########

open LOG , ">/tmp/nems_install_$fulldate.log";

# Get the OS name from the perl itself

print LOG " Getting the OS name...........\n";

my $osname=$Config{'osname'};

print LOG " OPERATING SYSTEM is $osname\n";
# Set the OS specific commands and parameters

print LOG " Setting the OS specific commands and parameters....\n";

if ( ($osname =~ /solaris/) || ($osname =~ /linux/) )
{
	$OS_LINK_CMD="ln -s ";
	$OS_PWD_CMD="pwd";
	$OS_OCM_STATUS=1;
}
elsif ($osname =~ /MSWin/) {
	$OS_LINK_CMD="move ";
	$OS_PWD_CMD="cd";
	$OS_OCM_STATUS=0;
}

my (@files);
my ($stmt);
my ($file);
my ($index);
my ($subfiles);

print LOG" Opening the Directory...\n";

opendir (DIR, '.') or die "Can't open dir $!\n";

if ((!@subfiles > 0) || ($_ =~ /y/))
{

opendir (DIR, '.') or die " we cant open dir $!\n";


@files = grep (/^ASE/, readdir(DIR));
if ($#files > 0 ) {

	print LOG"Found the following version ......\n";

	print "Found the following ASE root directories...\n";
	
	foreach $file (@files) {
		$index++;
		print "$index) $file\n";
	}

	print "Please select one of above directory for installation: ";
	$index = <STDIN>;
	chomp($index);	  
} else {
	$index = 1;
}

$stmt = "$OS_LINK_CMD" . $files[$index -1] . " ASESubsystem";
system($stmt);

open (MYFILE, "nems_install.cfg");
print LOG" opening nems_install.cfg\n";
while (<MYFILE>) 
{ 
  chomp; 
  # remove the newline character! 
  next if /^#/; 
  # skip the comment lines! 
  if (/^END/)
  {
    $parsing = 0;
  }

  if ((/^FILES/) || ($parsing==3))
  {
    print LOG" getting file name specified in nems_install.cfg\n";
    $parsing = 3;
    next if /^FILES/;
    @files = split(/:/);
  }
  elsif ((/^MACROS/) || ($parsing==1))
  {
    print LOG" getting macros specified in nems_install.cfg\n";
    $parsing = 1; 
    next if /^MACROS/;
    ($key, $question, $default) = split (/:/);   # split on ":" 
    $value = "";
    print LOG " macro is $key\n";
    while (length($value) == 0 )
    {
      ##
      # If default starts with a $ then check if we already have it
      # in memory.
      $_ = $default;
      if (/^\$/)
      {
        $default = $macro_hash{substr($_,1,length($default)-1)}; 
      }
      $value = prompt_user($question,$default);
      if (length($value) == 0 )
      {
        print "\nERR-->I am sorry, this entry is mandatory. Please try again.\n";
      }
    }

    if($osname =~ /MSWin/)
    {
	$value =~ s/\\/\//g;
    }
    $macro_hash{ $key } = $value; 
  }
  elsif ((/^PARAMS/) || ($parsing==2))
  {
     print LOG" getting parameters specified in nems_install.cfg\n";
     $parsing = 2; 
     next if /^PARAMS/;
     ($key, $question,$default) = split (/:/); # split on ":" 
     #print " $key = ";
    
     if ( $key eq '30.1.24' || $key eq '30.1.11' || $key eq '30.1.13')
     {
	print LOG" validating IP address for key = $key........................ \n";
	$value = "";
        while (length($value) == 0 )
        {
            $_ = $default;
            if (/^\$/)
            {
              $default = $param_hash{substr($_,1,length($_)-1)}; 
            }
          
            #$value = prompt_user($question,$default);
	    if( $key eq '30.1.24' )
	    {
		$value=$ENV{'MY_HOST_IP'};
	    }
	    if( $key eq '30.1.11' )
	    {
		$value=$ENV{'MY_HOST_SIG_IP'};
	    }
	    if( $key eq '30.1.13' )
	    {
		$value=$ENV{'MY_HOST_HTTP_IP'};
	    }
            if (length($value) == 0 )
            {
               print "\nERR-->I am sorry, this entry is mandatory. Please try again.\n";
            }
            else
            {
	        @IPADDRESS = split (/\./,$value);
		    @IPADDRESS_IPv6 = split (/:+/,$value);
	        $length = @IPADDRESS;
		    $length_ipv6 = @IPADDRESS_IPv6;
	       
                if ( $length != 4 && ( $length_ipv6 < 2 || $length_ipv6 > 8))
	        {
	               print "\n ERR--> I AM SORRY, BUT YOU HAVE ENTERED A WRONG VALUE..PLEASE TRY AGAIN.\n";
	               $value = "";
              }
	      elsif($length == 4)
	        {
		       #$_ = @IPADDRESS[0];
		       for ( $x=0;$x<4;$x++)
		       {
			      $_ = @IPADDRESS[$x];

                              if ( !(m/^[0123456789]*$/) || (m/^[ ]*$/))
		              {
			          print "\n ERR--> I AM SORRY BUT YOU AGAIN ENTERED A WRONG VALUE... PLEASE TRY AGAIN.\n";
			          $value = "";
	                      }
		       }
	         }	
		 else
		 {
			for ( $x=0;$x < $length_ipv6;$x++)
			{
			     if($x == 0 && $IPADDRESS_IPv6[0] eq "" )
				  {
				     next;
				  }
				 
			      $_ = @IPADDRESS_IPv6[$x];
				  
                              if ( !(m/^[0123456789abcdef\[\]]*$/i) || (m/^[ ]*$/))
		              {
			          print "\n ERR--> I AM SORRY BUT YOU AGAIN ENTERED A WRONG VALUE... PLEASE TRY AGAIN. \n";
			          $value = "";
	                      }
		       }

		    }
             }
        }
	print LOG " IP address for key $key validated.\n";   
    }                          ######### KEY ENDS HERE
    elsif ( $key eq '30.1.7' || $key eq '30.1.12')

    {
	print LOG" validating port number for key = $key \n";

	 if ( $key eq '30.1.7' || $key eq '30.1.12')
	 {
		print LOG" validating port number for key = $key................. \n";
	     $value = "";
	     while (length($value) == 0 )
	     {
		 $_ = $default;
		 if (/^\$/)
		 {
		     $default = $param_hash{substr($_,1,length($_)-1)}; 
		 }
		 #$value = prompt_user($question,$default);
		 if ($key eq '30.1.7')
		 {
			$value=$ENV{'MY_HOST_SIG_PORT'};
		 }
		 if($key eq '30.1.12')
		 {
			$value=$ENV{'MY_HOST_HTTP_PORT'};
		 }

		$_ =$value;
		 if (length($value) == 0 )
		 {
		     print "\nERR-->I am sorry, this entry is mandatory. Please try again.\n";
		 } elsif ( !(m/^[0123456789]*$/)) {	
		     print "\n ERR--> I AM SORRY, BUT THIS IS NOT A NUMERIC VALUE\n\n";
			$value = "";
		 }
	     }
	 }
	print LOG " Port number for key $key validated.\n";
     }
     else
	{
	     $value = "";
	     while (length($value) == 0 )
	     {
		 $_ = $default;
		 if (/^\$/)
		 {
		     $default = $param_hash{substr($_,1,length($_)-1)}; 

		 }
		 #$value = prompt_user($question,$default);
		 $value=$ENV{'CDR_WRITE_LOC'};

		$_ =$value;
		 if (length($value) == 0 )
		 {
		     print "\nERR-->I am sorry, this entry is mandatory. Please try again.\n";
		 }
	      }
		
	## This is just to make the path compatible with windows..because windows returns path with " \ " seperator
	## and not with " / " seperator.
	## So we need to replace " \" with " /" just to make the path compatible.
	## It is not used in case of SOLARIS\UNIX.

		if($osname =~ /MSWin/)
		{
			$value =~ s/\\/\//g;
		}
	}
	
   $param_hash{ $key } = $value; 
  }

  # This is for the hardcode values, as this installer is for Non FT
  # setup. This value is for the FT mode, which is non FT.
  $param_hash{"30.1.33"} = 1;
  $param_hash{"30.1.21"} = $OS_OCM_STATUS;
  $param_hash{"30.1.25"} = "127.0.0.1";

  # Now for the install root
  $_ = `$OS_PWD_CMD`;

  chomp;
	## This is just to make the path compatible with windows..because windows returns path with " \ " seperator
	## and not with " / " seperator.
	## So we need to replace " \" with " /" just to make the path compatible.
	## It is not used in case of SOLARIS\UNIX.
	
	# Bug BPInd18153
	# This was commented beacuse this install root was written in ase_no_ems.bat file, which had "\" seperator 
	# replaced with "/" which cant be recognized by windows.
	#	if($osname =~ /MSWin/)
	#	{
	#		$_ =~ s/\\/\//g;
	#	}
		
  $macro_hash{INSTALL_ROOT} = $_;
}
print LOG " closing nems_install.cfg\n";
close(MYFILE);

foreach $file (@files)
{
 print LOG " replacing macros and parameters values in file $file\n";
 while ( ($key, $value) = each(%macro_hash) ) 
 {
   print "$key = $value, file=$file\n";
   print LOG " Calling find_replace_macro for macro= $key\n";
   find_replace_macro ($key, $value, $file);    
 }
 while ( ($key, $value) = each(%param_hash) ) 
 {
   print LOG "Calling find_replace_equality for parameter = $key\n";
   find_replace_equality ($key, $value, $file);    
 }
}

print LOG"###########    FINISHED INSTALLING NON EMS CAS   #########\n";

close LOG;
print "\n#-------FINISHED INSTALLING NON EMS CAS---------\n\n\n";

#------------------------------------------#
# find and replace function for macros
#  ARGS:$find - search for this
#	$replace - replace with this
#	$glob - In these/this file
#------------------------------------------#

sub find_replace_macro
{
  print LOG "find_replace_macro called for macro= $key\n";
  my ($find,$replace,$glob) = @_;
 
  @filelist = <*$glob>;

 # This condition check was written keeping in view that user may enter a null value.
 # But the problem with this is that if value of any of $find or $replace or $glob is zero it quits.so we cant
 # put value of any parameter equal to zero.
 # As all validations are done beforehand in this code this condition check is no longer required.
 #


  # 
  #if ( (!$find) || (!$replace) || (!$glob) ) 
  #{
  #   print "\nERR--> Something wrong in FindAndReplace() fucntion\n";
  #   exit(0);
  #}
  

  
  # process each file in file list
  foreach $filename (@filelist) {

  
  	print "    For macros working on : $filename\n";
  
  	# retrieve complete file
      open (IN, "$filename") || die("Error Reading File: $filename $!");
  	{
  		undef $/;          
  		$infile = <IN>;
  	}
      close (IN) || die("Error Closing File: $filename $!");
  
  	$infile =~ s/$find/$replace/g;


	# write complete file

       open (PROD, ">$filename") || die("Error Writing to File: $filename $!");
  	 print PROD $infile;
       close (PROD) || die("Error Closing File: $filename $!");
  
  }
  
     print "Finished. Macro substitution\n\n";
}



#------------------------------------------#
# find and replace function for equality
#  ARGS:$find - search for this
#	$replace - replace with this
#	$glob - In these/this file
#------------------------------------------#

sub find_replace_equality
{
  print LOG " find_replace_equality called for parameter $key.\n";
  my $equal = "=";
  my ($find,$replace,$glob) = @_;
  

  @filelist = <*$glob>;

 #This condition check was written keeping in view that user may enter a null value.
 # But the problem with this is that if value of any of $find or $replace or $glob is zero it quits.so we cant 
 # put value of any parameter equal to zero. 
 # As all validations are done beforehand in this code this condition check is no longer required.
 #
   
 # if ( (!$find) || (!$replace) || (!$glob) ) 
 #{
 #   print " Something wrong in FindAndReplace() fucntion\n";
 #   exit(0);
 #}
  
  # process each file in file list
  foreach $filename (@filelist) {
  
  	print "    For params working on : $filename\n";
  
  	# retrieve complete file
      open (IN, "$filename") || die("Error Reading File: $filename $!");
  	{
		undef $/;          
  		$infile = <IN>;
  	}
      close (IN) || die("Error Closing File: $filename $!");
  
	$infile =~ s/$find$equal.*\n/$find$equal$replace\n/g;
  	
  
  	# write complete file 
       open (PROD, ">$filename") || die("Error Writing to File: $filename $!");
  	 print PROD $infile;
       close (PROD) || die("Error Closing File: $filename $!");
  
  }
  
     print "Finished. Equality substitution\n\n";
}





#-------------------------------------------------------------------------
#  ARGS:	$promptString - what you want to prompt the user with     
#		$defaultValue - (optional) a default value for the prompt 
#-------------------------------------------------------------------------

sub prompt_user 
{

	print LOG " INSIDE prompt_user \n";
   local($promptString,$defaultValue) = @_;

   #-------------------------------------------------------------------
   #  if there is a default value, use the first print statement; if   
   #  no default is provided, print the second string.                 
   #-------------------------------------------------------------------

   if ($defaultValue) {
      print $promptString, "   [", $defaultValue, "]: ";
   } else {
      print $promptString, ": ";
   }

   $| = 1;               # force a flush after our print
   $_ = <STDIN>;         # get the input from STDIN (presumably the keyboard)


   #------------------------------------------------------------------#
   # remove the newline character from the end of the input the user  #
   # gave us.                                                         #
   #------------------------------------------------------------------#

   chomp;

   #-----------------------------------------------------------------
   #  if we had a $default value, and the user gave us input, then   
   #  return the input; if we had a default, and they gave us no     
   #  no input, return the $defaultValue.                            
   #                                                                  
   #  if we did not have a default value, then just return whatever  
   #  the user gave us.  if they just hit the  key,           #
   #  the calling routine will have to deal with that.               
   #-----------------------------------------------------------------

   if ("$defaultValue") {
      return $_ ? $_ : $defaultValue;    # return $_ if it has a value
   } else {
      return $_;
   }
}
}








