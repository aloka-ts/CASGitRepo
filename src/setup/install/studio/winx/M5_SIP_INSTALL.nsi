
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
##     File:     M5_SIP_INSTALL.nsi
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
##################################################################################################################
	;START OF SCRIPT

	!include WinMessages.nsh
	!include FileFunc.nsh
	!include "MUI.nsh"
	!include "MUI2.nsh"
	!include "zipdll.nsh"
	!include "LogicLib.nsh"
	;if you want to set the environment variable for all user just remove the ; from the next line 
	!define ALL_USERS
	!include "WriteEnvStr.nsh" 
	!define MUI_ABORTWARNING
	!include "Sections.nsh"
	!define ASE_VER "ASE6.0.0"

;--------------------------------------------------------------------------------------------------------------
	;Main Settings about .exe files

	Name "GENBAND M5 SIP Application Server"
	OutFile "M5_SIP_INSTALL.exe"
	InstallDir "$PROGRAMFILES"

;----------------------------------------------------------------------------------------------------------
	;VARIABLES
	
	Var /GLOBAL v1 
	Var /GLOBAL v2 
	Var /GLOBAL v31
	Var /GLOBAL j_path
	Var /GLOBAL t1
	Var /GLOBAL t2
	Var /GLOBAL ct1
	Var /GLOBAL ct3
	Var /GLOBAL ct4
	Var /GLOBAL ct5
	Var /GLOBAL d1
	Var /GLOBAL d2
	Var /GLOBAL d3
	Var /GLOBAL flg2
	Var /GLOBAL hn1
	Var /GLOBAL hln2
	Var /GLOBAL hln3
	Var /GLOBAL eclipse
	Var /GLOBAL FH
	Var /GLOBAL FR
	Var /GLOBAL FR1
	Var /GLOBAL FC1
	Var /GLOBAL WP
	Var /GLOBAL WP1
	Var /GLOBAL SP
	Var /GLOBAL SP1
	Var /GLOBAL SL1
	Var /GLOBAL WBA
	Var /GLOBAL v3
	Var /GLOBAL v4
	Var /GLOBAL v5
	

;-------------------------------------------------------------------------------------------------------------
	;Pages
	
	!insertmacro MUI_PAGE_WELCOME
	;!insertmacro MUI_PAGE_LICENSE "${NSISDIR}\Docs\Modern UI\License.txt"
	Page Custom CustomCreate CustomLeave
   	Page Custom IPCreate IPLeave
	!insertmacro MUI_PAGE_DIRECTORY
        Page Custom confirmCreate confirmLeave 
	!insertmacro MUI_PAGE_INSTFILES
	!insertmacro MUI_PAGE_FINISH
	!insertmacro MUI_UNPAGE_WELCOME
	!insertmacro MUI_UNPAGE_CONFIRM
	!insertmacro MUI_UNPAGE_INSTFILES
	!insertmacro MUI_UNPAGE_FINISH

;--------------------------------------------------------------------------------------------------------
	;Languages
	
	!insertmacro MUI_LANGUAGE "English"
;--------------------------------------------------------------------------------------------------------
	;BACKGROUND COLOR
	
	BGGradient FFEEFF
	BrandingText  "GENBAND M5 Series"
;-------------------------------------------------------------------------------------------------------
	;CALLBACK FUNCTION


;-------------------------------------------------------------------------------------------------------
	;DIFFERENT SECTIONS

Section  "!${ASE_VER}" SecDummy
	
	#########################################################################################################
	#Resgistory Entries....
	ClearErrors
	WriteRegStr HKLM "SOFTWARE\GENBAND\" "" ""                             ;root_key subkey key_name value
	WriteRegStr HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\" "" ""
	WriteRegStr HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\ASE\" "CurrentVersion" "6.0.0" 
	WriteRegStr HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\ASE\6.0.0" "ASE_HOME" "$INSTDIR" 
	WriteRegStr HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\ASE\6.0.0" "JAVA_HOME" "$j_path"
	WriteRegStr HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\Plugins" "ECLIPSE_HOME" "$eclipse" 
	WriteRegStr HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\Plugins" "Version" "6.0.0" 

	StrCpy $FC1 0
	SetOutPath $INSTDIR
	
	###########################################################################################################
	ClearErrors
	FileOpen $FH "$TEMP\GENBAND\SipServlet6.0.0E\ASE6.0.0E\conf\ase.properties" a
	IfErrors 0 +2
		Quit
loop2:
	ClearErrors
	FileRead $FH $FR
	IfErrors loop11 0		;loop condition
	StrCpy $FR1 $FR 7 
	StrCmp $FR1 "30.1.24" GoAhead 0
	StrCmp $FR1 "30.1.13" GoAhead 0
	StrCmp $FR1 "30.1.11" GoAhead 0
	StrCmp $FR1 "30.1.7=" GoAhead 0
	StrCmp $FR1 "30.1.21" GoAhead 0
	StrCmp $FR1 "30.1.33" GoAhead 0
	StrCmp $FR1 "30.1.25" GoAhead 0
	StrCmp $FR1 "30.1.12" GoAhead loop2
				
GoAhead:
	StrCmp $FR1 "30.1.7=" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR$SP1"
		FileSeek $FH -$SL1 CUR  
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1
	StrCmp $FR1 "30.1.24" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR=$WBA"                    
		FileSeek $FH -$SL1 CUR         
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1
	StrCmp $FR1 "30.1.13" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR=$WBA "
		FileSeek $FH -$SL1 CUR         
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1
	StrCmp $FR1 "30.1.11" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR=$WBA "
		FileSeek $FH -$SL1 CUR         
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1
;----------------------------------------
	StrCmp $FR1 "30.1.21" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR=0 "
		FileSeek $FH -$SL1 CUR         
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1
	StrCmp $FR1 "30.1.33" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR=1 "
		FileSeek $FH -$SL1 CUR         
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1
	StrCmp $FR1 "30.1.25" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR=127.0.0.1 "
		FileSeek $FH -$SL1 CUR         
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1
;----------------------------------------------------
	StrCmp $FR1 "30.1.12" 0 +7
		StrLen $SL1 $FR
		StrCPy $FR $FR 7
		StrCpy $FR "$FR=$WP1"
		FileSeek $FH -$SL1 CUR
		FileWrite $FH  "$FR$\r$\n#"
		Goto check1

Check1:	
	Goto loop2
;---------------------------------------------------------------------------------------------------------
loop11:
	FileClose $FH
	Rename "$TEMP\GENBAND\SipServlet6.0.0E\ASE6.0.0E\" "$TEMP\GENBAND\SipServlet6.0.0E\ASESubsystem\"
	Push ASE_HOME
	push "$INSTDIR\GENBAND\SipServlet6.0.0E\ASESubsystem"
	Call WriteEnvStr
	FileOpen $FH "$TEMP\GENBAND\SipServlet6.0.0E\ASESubsystem\\scripts\ase_no_ems.bat" a
loop12:
	ClearErrors
	FileRead $FH $FR
	IfErrors loop10 0
		;messagebox MB_OK "$FR"
		StrCpy $FR1 $FR 16
		StrCmp $FR1 "SET INSTALLROOT=" 0 loop12
		;messagebox MB_OK "$FR1"
		StrLen $SL1 $FR
		FileSeek $FH -$SL1 CUR
		FileWrite $FH  "$FR1$INSTDIR\GENBAND$\r$\n#"
		FileClose $FH
loop10:

	##################################################################################################################
	ReadIniStr $d2 '$PLUGINSDIR\custom.ini' 'Field 8' 'State'
	StrCmp $d2 "0" +6 0
		Push JAVA_HOME
		StrCpy $j_path $d1
		push $j_path
		Call WriteEnvStr
		Goto false2
        ReadIniStr $d1 '$PLUGINSDIR\custom.ini' 'Field 2' 'State'
	StrCpy $j_path "$t1(Current Java_Home)"
	${If} $j_path == $d1
		StrCpy $j_path $j_path -19
		Push JAVA_HOME
		push $j_path
		Call WriteEnvStr
	${Else}
		Push JAVA_HOME
		StrCpy $j_path $d1
		push $j_path
		Call WriteEnvStr
	${EndIf}
	
	###################################################################################################################
	false2:
	
	Banner::show /NOUNLOAD "Copying files ..."
	CopyFiles /SILENT  $TEMP\Plugins $eclipse
	CopyFiles /SILENT  $TEMP\GENBAND $INSTDIR
	 
	Banner::destroy 
	WriteUninstaller $INSTDIR\GENBAND\Uninstall.exe
	
SectionEnd

 




Section "Start Menu Shortcuts " SecDummy2
  CreateDirectory "$SMPROGRAMS\GENBAND"
  CreateShortCut "$SMPROGRAMS\GENBAND\M5_SAS_Uninstall.lnk" "$INSTDIR\GENBAND\M5_SAS_Uninstall.exe" "" "$INSTDIR\uninstall.exe" 0

SectionEnd



;-------------------------------------------------------------------------------------------------------
;Descriptions
	;Language strings
	LangString DESC_SecDummy ${LANG_ENGLISH} "This will install JBoss Server in your Computer according to your choice."
	LangString DESC_SecDummy2 ${LANG_ENGLISH} "a short cut will be available in Start Menu for JBoss Server."

	;Assign language strings to sections
	!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${SecDummy} $(DESC_SecDummy)
	!insertmacro MUI_DESCRIPTION_TEXT ${SecDummy2} $(DESC_SecDummy2)
	!insertmacro MUI_FUNCTION_DESCRIPTION_END

;-----------------------------------------------------------------------------------------------------------------

 Section "Uninstall"
        RMDir  /r "$INSTDIR"
	Delete "$SMPROGRAMS\*.*"
	Push   "JAVA_HOME" 
	Call   un.DeleteEnvStr
	DeleteRegKey HKLM "SOFTWARE\GENBAND\M5 SIP Application Server\"   ;root_key subkey
	;Delete "$INSTDIR\Uninstall.exe"
SectionEnd

;------------------------------------------------------------------------------------------------------------------
	;FUNCTION STARTS

Function confirmCreate
 	 !insertmacro MUI_HEADER_TEXT_PAGE "Confirm Details" "Please Confirm the details that will be used by the Installer."
	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Settings' 'NumFields' '7'
                                                                                                                                                              
	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 1' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 1' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 1' 'Top' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 1' 'Right' '239' ;239 
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 1' 'Bottom' '15'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 1' 'Text' \
         ' The option that you have selcted are shown as below:'   
 
	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 2' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 2' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 2' 'Top' '25'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 2' 'Right' '239' ;239 
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 2' 'Bottom' '35'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 2' 'Text' \
	 '1. Java_Home   : $j_path '

	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 3' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 3' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 3' 'Top' '45'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 3' 'Right' '539'  ;239 
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 3' 'Bottom' '55'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 3' 'Text' \
	 '2. Eclipse Dir     : $eclipse ' 
	
	 

	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 4' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 4' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 4' 'Top' '65'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 4' 'Right' '239' ;239 
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 4' 'Bottom' '75'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 4' 'Text' \
	 '3. Bind Address : $WBA'
							

	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 5' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 5' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 5' 'Top' '85'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 5' 'Right' '239'  ;239 
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 5' 'Bottom' '95'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 5' 'Text' \
	 '4. WEB Port      : $WP1 '

	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 6' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 6' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 6' 'Top' '105'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 6' 'Right' '239'  ;239 
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 6' 'Bottom' '115'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 6' 'Text' \
	 '4. SIP Port        : $SP1 '


	 WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 7' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 7' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 7' 'Top' '125'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 7' 'Right' '539' 
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 7' 'Bottom' '135'
         WriteIniStr '$PLUGINSDIR\custom2.ini' 'Field 7' 'Text' \
	 '5. Install Dir       : $INSTDIR'
	 

	 push $0
         InstallOptions::Dialog '$PLUGINSDIR\custom2.ini'
FunctionEnd

Function confirmLeave
FunctionEnd


Function CustomCreate
	 
	 !insertmacro MUI_HEADER_TEXT_PAGE "Choose Paths" "Please select the appropriate Paths to JRE and Eclipse Directories"
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Settings' 'NumFields' '10' 
	 
 
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 1' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 1' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 1' 'Top' '5'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 1' 'Right' '-6'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 1' 'Bottom' '17'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 1' 'Text' \
         'JAVA HOME:'
  
	
	 Call DetectJre

	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'Type' 'DropList'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'Left' '60'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'Top' '20'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'Right' '-31'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'Bottom' '90'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'Flags' 'Notify'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'State' '$v31'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 2' 'ListItems' '$v1'

	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 3' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 3' 'Left' '1'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 3' 'Top' '109'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 3' 'Right' '40'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 3' 'Bottom' '119'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 3' 'Text' \
         'Browse '

	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'Type' 'DirRequest'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'Left' '60'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'Top' '59'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'Right' '-31'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'Bottom' '69'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'Flags' 'Notify|DISABLED'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'Text' 'BROWSE FOR JRE DIRECTORY'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 4' 'State' '$d3' 
	 
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 5' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 5' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 5' 'Top' '89'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 5' 'Right' '-6'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 5' 'Bottom' '99'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 5' 'Text' \
         'ECLIPSE HOME:'

	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 6' 'Type' 'DirRequest'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 6' 'Left' '60'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 6' 'Top' '109'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 6' 'Right' '-31'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 6' 'Bottom' '119'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 6' 'Text' 'BROWSE FOR ECLIPSE DIRECTORY'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 6' 'State' '$eclipse' 


	 
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 7' 'Type'  'RadioButton' 
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 7' 'Left' '40'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 7' 'Top' '20'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 7' 'Right' '50'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 7' 'Bottom' '30'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 7' 'Flags' 'Notify'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 7' 'State' '1' 

	
		 
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 8' 'Type'  'RadioButton' 
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 8' 'Left' '40'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 8' 'Top' '59'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 8' 'Right' '50'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 8' 'Bottom' '69'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 8' 'Flags' 'Notify'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 8' 'State' '0' 



	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 9' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 9' 'Left' '0'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 9' 'Top' '20'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 9' 'Right' '30'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 9' 'Bottom' '90'
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 9' 'Text' 'Available'

	 
	 WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 10' 'Type'  'Label' 
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 10' 'Left' '0'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 10' 'Top' '59'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 10' 'Right' '30'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 10' 'Bottom' '69'
         WriteIniStr '$PLUGINSDIR\custom.ini' 'Field 10' 'Text' 'Browse'
	


         push $0
         InstallOptions::Dialog '$PLUGINSDIR\custom.ini'
	
FunctionEnd



Function CustomLeave
	ReadIniStr $0 '$PLUGINSDIR\custom.ini' 'Settings' 'State'
	StrCmp $0 0 E_dir
	StrCmp $0 7 Radio7
	StrCmp $0 8 Radio8
	StrCmp $0 6 E_dir
	StrCmp $0 2 0
	Abort

##################################################################################################################	
E_dir:
	ReadIniStr $eclipse '$PLUGINSDIR\custom.ini' 'Field 6' 'State'
	IfFileExists $eclipse\eclipse.exe PathGood1
		MessageBox MB_ICONEXCLAMATION|MB_OK "Please enter a valid Eclipse path."
		Abort 
PathGood1:
	IfFileExists $eclipse\plugins PathGood2
		MessageBox MB_ICONEXCLAMATION|MB_OK "Please enter a valid Eclipse path."
		Abort
PathGood2:
	StrCmp $0 0 next 0
	Abort
	
##################################################################################################################
Radio7:
 ReadINIStr $0 "$PLUGINSDIR\custom.ini" "Field 7" "State"
 StrCmp $0 "0" next1 0
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 2" "HWND"
	EnableWindow $1 1
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND"
	EnableWindow $1 0
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND2"
	EnableWindow $1 0
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 7" "State" "1"
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 8" "State" "0"
	Abort
Next1:
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 2" "HWND"
	EnableWindow $1 0
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND"
	EnableWindow $1 1
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND2"
	EnableWindow $1 1
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 7" "State" "0"
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 8" "State" "1"
	Abort
Radio8:
 ReadINIStr $0 "$PLUGINSDIR\custom.ini" "Field 8" "State"
 StrCmp $0 "0" next7 0
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 2" "HWND"
	EnableWindow $1 0
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND"
	EnableWindow $1 1
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND2"
	EnableWindow $1 1
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 7" "State" "0"
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 8" "State" "1"
	Abort
Next7:
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 2" "HWND"
	EnableWindow $1 1
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND"
	EnableWindow $1 0
	ReadINIStr $1 "$PLUGINSDIR\custom.ini" "Field 4" "HWND2"
	EnableWindow $1 0
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 7" "State" "1"
	!insertmacro INSTALLOPTIONS_WRITE "$PLUGINSDIR\custom.ini" "Field 8" "State" "0"
	Abort
##################################################################################################################
next:
	ReadIniStr $d3 '$PLUGINSDIR\custom.ini' 'Field 4' 'State'
	ReadIniStr $d2 '$PLUGINSDIR\custom.ini' 'Field 8' 'State'
	StrCmp $d2 "0" +4 0
		ReadIniStr $d1 '$PLUGINSDIR\custom.ini' 'Field 4' 'State'
		StrCpy $j_path $d1
		Goto End
        ReadIniStr $d1 '$PLUGINSDIR\custom.ini' 'Field 2' 'State'
	StrCpy $j_path "$t1(Current Java_Home)"
	${If} $j_path == $d1
		StrCpy $j_path $j_path -19
	${Else}
		StrCpy $j_path $d1
	${EndIf}
	StrCpy $v31 $d1
	End:

FunctionEnd


Function DetectJre
	
	strcpy $ct1 0
	StrCpy $flg2 0
	StrCpy $5 0
	StrCpy $ct3 0
 	;setting the flag to check whether jre is installed or not and to see the index depth of the root directory. 
	; to check whether client machine has the upadated version that is version greator then 1.4
 	EnumRegValue $v1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" 0
	ReadRegStr $v2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" $v1
	StrCpy $v2 $v2 "" 2
	IntCmp $v2 "4" 0 0  loop
		MessageBox MB_OK "Sorry! You dont have the Updated jre Installed.$\n You need to update it now.$\npress OK to terminate." 
		Quit
	;loop to check all possible values of jre in client machine
loop:
	;to check for available jdk versions
	
	EnumRegKey $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" $R2
	StrCmp $1 "" done
	StrCpy $t2 $1 1 2
	IntCmp $t2 4 0 0 +2
		Goto ok1
	EnumRegValue $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" 0
	ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" $2
	;----------------------------------------------------------------------------------------------------
	;check for java.exe 
	ClearErrors
	FindFirst $8 $9 $3\bin\java.exe ;user_var(handle output) user_var(filename output) filespec
	IfErrors ok1 0
	ReadEnvStr $t1 JAVA_HOME
	StrCmp $3 $t1  0 +2
		StrCpy $3 "$3(Current Java_Home)"
	StrCmp $ct1 '0' 0 next
		StrCpy $R3 '$R4'
		StrCpy $R3 '$R4'
		StrCpy $v31 '$3'
		IntOp $ct1 $ct1 + 1
next:
	StrCpy $6 '$6|$3'
	StrCpy $flg2 1
ok1:
	IntOp $R2 $R2 + 1
	Goto loop
done:
	;StrCpy $6 $6 "" 1           
	Strcpy $v1 "$6|"
	IntCmp $R2 0  ok false false
ok:
	MessageBox MB_OK "Sorry! You dont have the jre Installed.$\nYou need to install it now.Unable to proceed." 
false:
FunctionEnd


Function IPCreate
	
 	WriteIniStr '$PLUGINSDIR\custom1.ini' 'Settings' 'NumFields' '6'
	WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 1' 'Type' 'Label'
	WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 1' 'Left' '5'
	WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 1' 'Top' '22'
	WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 1' 'Right' '155'
	WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 1' 'Bottom' '100'
	WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 1' 'Text' \
	'Select Web Listner Bind Address:'
  
	Call DetectIP
  
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'Type' 'DropList'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'Left' '160'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'Top' '22'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'Right' '250'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'Bottom' '100'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'Flags' 'Notify'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'State' '$WBA'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 2' 'ListItems' '$ct4'

 
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 3' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 3' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 3' 'Top' '70'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 3' 'Right' '150'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 3' 'Bottom' '80'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 3' 'Text' \
         'Web Listner Port:'

	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'Type' 'Text'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'Left' '160'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'Top' '70'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'Right' '200'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'Bottom' '80'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'State' '$WP1'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'MaxLen' '5'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'MinLen' '1'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'Flags' 'ONLY_NUMBERS'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 4' 'ValidateText' ''

	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 5' 'Type' 'Label'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 5' 'Left' '5'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 5' 'Top' '100'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 5' 'Right' '150'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 5' 'Bottom' '110'
         WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 5' 'Text' \
         'SIP Listner Port:'

	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'Type' 'Text'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'Left' '160'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'Top' '100'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'Right' '200'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'Bottom' '110'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'State' '$SP1'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'MaxLen' '5'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'MinLen' '1'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'Flags' 'ONLY_NUMBERS'
	 WriteIniStr '$PLUGINSDIR\custom1.ini' 'Field 6' 'ValidateText' ''


	 push $0
         InstallOptions::Dialog '$PLUGINSDIR\custom1.ini'
FunctionEnd




Function IPLeave
	ReadIniStr $0 '$PLUGINSDIR\custom1.ini' 'Settings' 'State'
        StrCmp $0 '2' 0 next1
	     Abort
next1:

	ReadIniStr $WP1 '$PLUGINSDIR\custom1.ini' 'Field 4' 'State'
	IntCmp $WP '65535' +3 +3 0
	MessageBox MB_OK "Sorry! You can not enter Web Listner Port value more then '65535'." 
	Abort
	##########################################################################################################
	ReadIniStr $SP1 '$PLUGINSDIR\custom1.ini' 'Field 6' 'State'
	IntCmp $SP '65535' +3 +3 0
	MessageBox MB_OK "Sorry! You can not enter SIP Listner Port value more then '65535'." 
	Abort
	##########################################################################################################
	ReadIniStr $WBA '$PLUGINSDIR\custom1.ini' 'Field 2' 'State'

FunctionEnd



Function DetectIP
	StrCpy $hln2 0
	ip::get_ip
	Pop $ct4
	StrLen $ct5 $ct4
	IntOp $ct5 $ct5 - 1
	${While} $hln2 <= $ct5 
		StrCpy $hn1 $ct4 1 $hln2
		IntOp $hln2 $hln2 + 1
		${If} $hn1 == ";"
			IntOp $hln3 $hln2 - 1
			StrCpy $ct4 $ct4 $hln3 ""
			StrCpy $ct4 "$ct4|" 
		${EndIf}
	${EndWhile}
	StrCpy $ct4 "localhost|$ct40.0.0.0|"
FunctionEnd



Function .onInit
;-----------------------------------------------------------
/*    SetOutPath $TEMP
    File /oname=deka.skf "deka.skf"
    NSIS_SkinCrafter_Plugin::skin /NOUNLOAD $TEMP\deka.skf
    Delete $TEMP\deka.skf*/

;--------------------------------------------------------------
	InitPluginsDir
	GetTempFileName $9
	Rename $9 '$PLUGINSDIR\custom1.ini'
	StrCpy $eclipse "Required"
	StrCpy $d2 "Optional"
	StrCpy $WBA "localhost"
	StrCpy $WP1 "8080"
	StrCpy $SP1 "5060"

FunctionEnd

;-------------------------------------------------------------------------------
/*Function .onGUIEnd
	NSIS_SkinCrafter_Plugin::destroy
FunctionEnd

Function un.onInit
    ;User defined skin
    ;SetOutPath $TEMP
    ;File /oname=Stylish.skf "Stylish.skf"
    ;NSIS_SkinCrafter_Plugin::skin /NOUNLOAD $TEMP\Stylish.skf
    ;Delete $TEMP\Stylish.skf

    ;Default Skin
    NSIS_SkinCrafter_Plugin::skin /NOUNLOAD
FunctionEnd

Function un.onGUIEnd
	NSIS_SkinCrafter_Plugin::destroy
FunctionEnd*/
;-------------------------------------------------------------------------------

;END OF SCRIPT
####################################################################################################
