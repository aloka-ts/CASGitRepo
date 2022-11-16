#/######################################################################
##        GENBAND, Inc. Confidential and Proprietary
##
## This work contains valuable confidential and proprietary
## information.
## Disclosure, use or reproduction without the written authorization of
## GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
## is protected by the laws of the United States and other countries.
## If publication of the work should occur the following notice shall
## apply:
##
## "Copyright 2007 GENBAND, Inc.  All rights reserved."
#######################################################################
###/
#
#/######################################################################
##
##     Project:  SAS
##
##     File:     M5_SIP_SETUP.nsi
##
##     Desc:     NSIS script for winx installer
##
##     Author    Date                Description
##    ---------------------------------------------------------
##     Vaibhav	January 28, 2008   Initial Creation
##
#######################################################################
###/
#
!include "MUI.nsh"

Name "M5 SIP Application Server" 
OutFile "SIP SETUP.exe"
Var /GLOBAL v4

Section "" 
SectionEnd
BGGradient FFEEFF
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_DEFAULT MUI_INSTFILESPAGE_PROGRESSBAR "smooth"
################################################################################################################
#this will contain "SipServlet6.0.0.tar.gz" and "M5_SIP_INSTALL.exe" and internally call the M5_SIP_INSTALL.exe#
################################################################################################################

Function .onInit
	ReadRegStr $v4 HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\ASE\" "CurrentVersion"  
	StrCmp $v4 "" +3 0 
	MessageBox MB_ICONEXCLAMATION|MB_OK "Please Uninstall the M5 SIP Application Server Version-$v4 Before Installation."
	Quit
	Banner::show /NOUNLOAD "Copying files ..."
	setoutpath $TEMP
	File SAS6.0.0.1E_130308.zip
	File "M5_SIP_INSTALL.exe"
	File "SAS_IDE_PLUGIN_6.0.0.1_130308.zip"
	Banner::destroy
	Banner::show /NOUNLOAD "Extracting files ..."
	
	nsisunz::Unzip "$TEMP\SAS6.0.0.1E_130308.zip" "$TEMP\GENBAND"
	nsisunz::Unzip "$TEMP\SAS_IDE_PLUGIN_6.0.0.1_130308.zip" $TEMP

	Banner::destroy
	ExecWait "$TEMP\M5_SIP_INSTALL.exe"
	;----------------------------------------------------
	RMDir  /r "$TEMP\GENBAND\"
	RMDir  "$TEMP\GENBAND"
	RMDir  /r "$TEMP\Plugins"
	Delete "$TEMP\SAS_IDE_PLUGIN_6.0.0.1_130308.zip" 
	Delete "$TEMP\SAS6.0.0.1E_130308.zip" 
	Delete "$TEMP\M5_SIP_INSTALL.exe"
	Quit
FunctionEnd

 
