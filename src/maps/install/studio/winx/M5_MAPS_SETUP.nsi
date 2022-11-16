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
##     Project:  MAPS
##
##     File:     M5_MAPS_SETUP.nsi
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
OutFile "MAPS SETUP.exe"
Var /GLOBAL v4

Section "" 
SectionEnd
BGGradient FFEEFF
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_DEFAULT MUI_INSTFILESPAGE_PROGRESSBAR "smooth"
################################################################################################################
#this will contain "MAPS6.0.0.zip" and "M5_MAPS_INSTALL.exe" and internally call the M5_MAPS_INSTALL.exe#
################################################################################################################

Function .onInit
	ReadRegStr $v4 HKLM "SOFTWARE\GENBAND\M5 Multimedia Provisioning Application Server\MAPS\" "CurrentVersion"  
	StrCmp $v4 "" +3 0 
	MessageBox MB_ICONEXCLAMATION|MB_OK "Please Uninstall the M5 MAPS Application Server Version-$v4 Before Installation."
	Quit
	Banner::show /NOUNLOAD "Copying files ..."
	setoutpath $TEMP
	File MAPS6.0.0.2_020508.zip
	File M5_MAPS_INSTALL.exe
	File M5_MAPS_IDE_PLUGIN_6.0.0.2_020508.zip
	File oracle-ds.xml
	File hypersonic-ds.xml
	File "Start_Server.bat"
	File "Stop_Server.bat"
	Banner::destroy
	Banner::show /NOUNLOAD "Extracting files ..."
	nsisunz::Unzip "$TEMP\MAPS6.0.0.2_020508.zip" "$TEMP\GENBAND\MAPS6.0.0.2\"
	nsisunz::Unzip "$TEMP\M5_MAPS_IDE_PLUGIN_6.0.0.2_020508.zip" $TEMP
	Banner::destroy
	ExecWait "$TEMP\M5_MAPS_INSTALL.exe"
	;----------------------------------------------------
	RMDir  /r "$TEMP\GENBAND\"
	RMDir  "$TEMP\GENBAND"
	RMDir  /r "$TEMP\Plugins"
	Delete "$TEMP\M5_MAPS_IDE_PLUGIN_6.0.0.2_020508.zip" 
	Delete "$TEMP\MAPS6.0.0.2_020508.zip" 
	Delete "$TEMP\M5_MAPS_INSTALL.exe"
	Delete "$TEMP\oracle-ds.xml"
	Delete "$TEMP\hypersonic-ds.xml"
	Delete "$TEMP\Start_Server.bat"
	Delete "$TEMP\Stop_Server.bat"
	Quit
FunctionEnd

 
