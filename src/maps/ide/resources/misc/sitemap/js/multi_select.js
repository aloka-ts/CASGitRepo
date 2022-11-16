
var DAY = '11';
var MONTH = 'January';
var YEAR = '2008';
var HOURS = '21';
var MINUTES = '9';
var SECONDS = '26';

function ResetPassword(){
   openResWindow('ResetPassword.jsp?accountId='+document.formName.accountId.value,500,200);
}
function emailCheck (emailStr) 
{
var checkTLD=1;
var knownDomsPat=/^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum)$/;
var emailPat=/^(.+)@(.+)$/;
var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";
var validChars="\[^\\s" + specialChars + "\]";
var quotedUser="(\"[^\"]*\")";
var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;
var atom=validChars + '+';
var word="(" + atom + "|" + quotedUser + ")";
var userPat=new RegExp("^" + word + "(\\." + word + ")*$");
var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");
var matchArray=emailStr.match(emailPat);
if (matchArray==null) 
{
	//alert(document.formName.js16.value);
	return false;
}
var user=matchArray[1];
var domain=matchArray[2];

for (i=0; i<user.length; i++) 
{
	if (user.charCodeAt(i)>127) 
	{
		//alert(document.formName.js17.value);
		return false;
	}
}
for (i=0; i<domain.length; i++) 
{
	if (domain.charCodeAt(i)>127) 
	{
		//alert(document.formName.js18.value);
		return false;
	}
}

if (user.match(userPat)==null) 
{
	//alert(document.formName.js19.value);
	return false;
}

var IPArray=domain.match(ipDomainPat);
if (IPArray!=null) 
{
	for (var i=1;i<=4;i++) 
	{
		if (IPArray[i]>255) 
		{
			//alert(document.formName.js20.value);
			return false;
		}	
	}
	return true;
}
 
var atomPat=new RegExp("^" + atom + "$");
var domArr=domain.split(".");
var len=domArr.length;
for (i=0;i<len;i++) 
{
	if (domArr[i].search(atomPat)==-1) 
	{
		//alert(document.formName.js26.value);
		return false;
	}
}

if (checkTLD && domArr[domArr.length-1].length!=2 && domArr[domArr.length-1].search(knownDomsPat)==-1) 
{
	//alert("The address must end in a well-known domain or two letter " + "country.");
	//alert(document.formName.js21.value);
	return false;
}

if (len<2) 
{
	//alert("This address is missing a hostname!");
	//alert(document.formName.js22.value);
	return false;
}

return true;
}

function isAlphanumeric (s)
{   
	var reAlphanumeric = /^[a-zA-Z0-9_]+$/
    if ((s == null) || (s.length == 0)) 
       if (isAlphanumeric.arguments.length == 1) return false;
       else return (isAlphanumeric.arguments[1] == true);
    else {
       return reAlphanumeric.test(s)
    }
}

function mySubmit(elem){
	if((elem == "add") || (elem == "modify")){
		missinginfo = "";
		missinginfo1 = "";
		if (document.formName.customerId.value == "") {
			missinginfo += "\n     -  Customer ID";
		}
		else if(isAlphanumeric(document.formName.customerId.value) == false)
		{
			alert(document.formName.js28.value);
			return;
		}
		if (document.formName.firstName.value == "") {
			document.formName.firstName.value=document.formName.firstName.value;
		missinginfo += "\n     -  "+ document.formName.js1.value;
		}
		else if(isAlphanumeric(document.formName.firstName.value) == false)
		{
			alert(document.formName.js29.value);
			return;
		}
		if (document.formName.activationDate.value == "") {
		missinginfo += "\n     -  "+ document.formName.js2.value;

		}
		 if (document.formName.expirationDateTemp.value == "") {
		 missinginfo += "\n     -  "+ document.formName.js3.value;
		 }		
		if (document.formName.signInNameTemp.value == "") {
			document.formName.signInNameTemp.value=document.formName.signInNameTemp.value
		missinginfo += "\n     -  "+ document.formName.js4.value;

		}
		else if(elem == "add" && isAlphanumeric(document.formName.signInNameTemp.value) == false)
		{
		 alert(document.formName.js32.value);
		 return;
		}

		if(!(document.formName.nonEditableRoles) && document.formName.selectedRoles)
		{
			selectAll(document.formName.selectedRoles)
	 		//Bug Id BPInd09689
			var isCSRpresent = 0 ; 
			var isTicketingPresent = false ; 
	 		var newAreaCodes = document.formName.selectedRoles;
	 		if(countSelectedItems() > 1){
				for(i=newAreaCodes.length-1;i>=0;i=i-1)
				{
					var option = newAreaCodes.options[i];
					if(option.selected)
					{
						selectedValue = option.value;
						//alert(selectedValue.substring(0, selectedValue.indexOf("#")))
						selectedValue = selectedValue.substring(0, selectedValue.indexOf("#"));
						if(selectedValue == document.formName.ManagerRoleId.value || selectedValue == document.formName.AgentRoleId.value || selectedValue == document.formName.SupervisorRoleId.value)
						{
							isCSRpresent = isCSRpresent+1 ; 									
						}
						if(selectedValue == document.formName.TicketingRoleId.value)
						{
							isTicketingPresent = true ; 
						}

					}
				}
				//Added Support for BPInd17829
				if((isTicketingPresent == true && isCSRpresent > 1) || (isTicketingPresent == false && isCSRpresent > 0) )
				{
					option.selected = false;
					alert(document.formName.js30.value);
					return;
				}
	 		}else if( countSelectedItems() == 1 ){
				var option = document.formName.selectedRoles.options[0];
				var rptRole = option.value;
				var roleId = rptRole.substring(0, rptRole.indexOf("#"));
				var temp = rptRole.substring(rptRole.indexOf("#")+1);
				var roleName = temp.substring(0,temp.indexOf("#"));
				if( roleId == document.formName.NPReportsRoleId.value || roleId == document.formName.SPReportsRoleId.value ||
					roleId == document.formName.AgentReportsRoleId.value )
				{
					option.selected = false;
					alert(roleName+' '+document.formName.js31.value);
					return;
				}
				
			}
	 		//end fix Bug Id BPInd09689
	 		var assignedRole = getSelectedValue(document.formName.selectedRoles);
	 		if (assignedRole == null || assignedRole == "") {
	 			missinginfo += "\n     -  "+ document.formName.js27.value;
	 		}
 		}
		if(elem != 'modify'){
			if (document.formName.password.value == "") {
			missinginfo += "\n     -  "+ document.formName.js5.value;
			}
			if (document.formName.confirmPassword.value == "") {
			missinginfo += "\n     - "+ document.formName.js6.value;
			}
			if(document.formName.password.value !=document.formName.confirmPassword.value){
			missinginfo += "\n     - "+ document.formName.js7.value;
			}
		}
		 if ((document.formName.contact1.value == "" && document.formName.phone1.value != "") || (document.formName.phone1.value == "" && document.formName.contact1.value != "" )){
		 missinginfo += "\n     -  "+ document.formName.js8.value;
		 }
		 if ((document.formName.contact2.value == "" && document.formName.phone2.value != "") || (document.formName.phone2.value == "" && document.formName.contact2.value != "" )){
		 missinginfo += "\n     -  "+ document.formName.js9.value;
		 }
		 if ((document.formName.contact3.value == "" && document.formName.phone3.value != "") || (document.formName.phone3.value == "" && document.formName.contact3.value != "" )){
		 missinginfo += "\n     -  "+ document.formName.js10.value;
		 }
		 if ((document.formName.contact4.value == "" && document.formName.phone4.value != "") || (document.formName.phone4.value == "" && document.formName.contact4.value != "" )){
		 missinginfo += "\n     -  "+ document.formName.js11.value;
		 }
		//modified for Email-Validation
		if (document.formName.alternateEmailAddress.value != "" )
		{
			if (emailCheck(document.formName.alternateEmailAddress.value) == false )
			{
				missinginfo += "\n     -  "+ document.formName.js23.value;
			}
		}	
		if( document.formName.cellPhoneEmailAddress.value != "")
		{
			if (emailCheck(document.formName.cellPhoneEmailAddress.value) == false )
			{
				//missinginfo += "\n     -  Cell Phone Email Address is Not Valid ";
				missinginfo += "\n     -  "+ document.formName.js24.value;
			}
		}
		if( document.formName.pagerEmail.value != "" )
		{
			if (emailCheck(document.formName.pagerEmail.value) == false )
			{
				//missinginfo += "\n     -  Pager Email Address is Not Valid ";
				missinginfo += "\n     -  "+ document.formName.js25.value;
			}
		}

if(document.formName.contact1.value != ""){
	if(document.formName.contact1.value == document.formName.contact2.value || document.formName.contact1.value== document.formName.contact3.value || document.formName.contact1.value == document.formName.contact4.value){
 		missinginfo1 += "\n     -  1";
	}
}
if(document.formName.contact2.value != ""){
	if((document.formName.contact2.value)==(document.formName.contact1.value) || (document.formName.contact2.value)==(document.formName.contact3.value) || (document.formName.contact2.value)==(document.formName.contact4.value)){
 	missinginfo1 += "\n     -  2";
}
}
if(document.formName.contact3.value != ""){
	if((document.formName.contact3.value)==(document.formName.contact1.value) || (document.formName.contact3.value)==(document.formName.contact2.value) || (document.formName.contact3.value)==(document.formName.contact4.value)){
 	missinginfo1 += "\n     -  3";
}
}
if(document.formName.contact4.value != ""){
	if((document.formName.contact4.value)==(document.formName.contact1.value) || (document.formName.contact4.value)==(document.formName.contact2.value) || (document.formName.contact4.value)==(document.formName.contact3.value)){
 	missinginfo1 += "\n     -  4";
}
}
if(missinginfo1 != ""){
	missinginfo += "\n     -  "+ document.formName.js12.value;
}
		if (missinginfo != "") {
			missinginfo = document.formName.js13.value + "\n" +
			missinginfo + "\n"+ document.formName.js14.value;
			alert(missinginfo);
			return false;
		}else{
              //Expdate();
               Expdate1(document.formName.GMTactivationDate,document.formName.activationDate,false);
              Expdate1(document.formName.GMTexpirationDateTemp,document.formName.expirationDateTemp,true);
                  var ok = "yes";
// var Date1 = new Date(document.formName.expirationDate.value.substring(3,6)+" "+document.formName.expirationDate.value.substring(0,2)+" "+document.formName.expirationDate.value.substring(7,11));
		//	var Date2 = new Date(document.formName.activationDate.value.substring(3,6)+" "+document.formName.activationDate.value.substring(0,2)+" "+document.formName.activationDate.value.substring(7,11));

 var Date1 = getJSDateFromDateString(document.formName.GMTexpirationDateTemp.value);
 var Date2 = getJSDateFromDateString(document.formName.GMTactivationDate.value);
			var day1 = Date1.getDate();
			var day2 = Date2.getDate();
			var month1 = Date1.getMonth();
			var month2  = Date2.getMonth();
			var year1  = Date1.getFullYear();
			var year2  = Date2.getFullYear();
			if(year2 > year1)
			{
				   ok = "no";
			}
			else if(year2 < year1)
			{
			   ok = "yes";
			}
			else
			{
                           if(month2 > month1)
				   ok = "no";
				else if(month2 < month1)
				   ok = "yes";
				else
				{
						if(day2 > day1)
						  ok = "no";
						else
						  ok = "yes";
				}
			}
			if (ok == "no") {
				alert(document.formName.js15.value);
			}else{
   	          		//Expdate();
                        Expdate1(document.formName.GMTactivationDate,document.formName.activationDate,false);
                        Expdate1(document.formName.GMTexpirationDateTemp,document.formName.expirationDateTemp,true);
			if(elem == "add"){
					if(document.formName.parentDomain.value == "")
						document.formName.signInName.value = document.formName.signInNameTemp.value;
					else
						document.formName.signInName.value = document.formName.signInNameTemp.value+"@"+ document.formName.parentDomain.value;
				}
				if(!(document.formName.nonEditableRoles) && document.formName.selectedRoles){
				selectAll(document.formName.selectedRoles)
				}
		      	document.formName.action.value=elem
		  		document.formName.submit()
		}
			}
}
}

					function move(from, to)
					{
						for(i=from.length-1;i>=0;i=i-1)
						{
							var selected = from.options[i];
							if(selected.selected)
							{
								alert(selected.value)
								//selected.text = removeDefaultText(selected.text);
								to.options[to.options.length] = new Option(selected.text, selected.value, false, false);
								from.options[i] = null;
							}
						}
					}
					
					function moveRight()
					{
						move(document.formName.availableRoles ,document.formName.selectedRoles);
					}
					
					function setSelectedValue(selectedValue, selectBox)
					{
						//var tmp ="";
						for(i=selectBox.length-1;i>=0;i=i-1)
						{
							var option = selectBox.options[i];
							//tmp += "-"+option.value+"-";
							if(option.value == selectedValue)
							{
								option.selected = true
								return option;
							}
						}
						return null;
					}
					
					function moveLeft()
					{
						var selectBox = document.formName.selectedRoles;
						for(i=selectBox.length-1;i>=0;i=i-1)
						{
							var option = selectBox.options[i];
							//tmp += "-"+option.value+"-";
							if(option.selected && option.text.indexOf(" - (") != -1)
							{
								selectBox.options[i] = null;
							}
						}
						move(document.formName.selectedRoles ,document.formName.availableRoles);
					}
					
					function selectAll(selectBox)
					{
						for(i=0;i<selectBox.length;i++)
							selectBox.options[i].selected = true;
					}
					
					function moveAllRight()
					{
						selectAll(document.formName.availableRoles);
						moveRight();
					}
					
					function moveAllLeft()
					{
						selectAll(document.formName.selectedRoles);
						moveLeft();
					}
					
					/*function swapOptions(newAreaCodes, i, j)
					{
						var tmpOption1 = newAreaCodes.options[j];
						var tmpOption2 = newAreaCodes.options[i];
						newAreaCodes.options[j] = new Option("", "", false, false);
						newAreaCodes.options[i] = new Option("", "", false, false);
						newAreaCodes.options[j] = tmpOption2;
						newAreaCodes.options[i] = tmpOption1;
					}
					
					function moveUp()
					{
						var newAreaCodes = document.formName.newAreaCodes;
						for(i=0;i<newAreaCodes.length;i++)
						{
							var option = newAreaCodes.options[i];
							if(option.selected && i != 0)
							{
								swapOptions(newAreaCodes, i, i-1);
							}
							else if(option.selected)
							{
								option.selected = false;
							}
						}
					}
					
					function moveDown()
					{
						var newAreaCodes = document.formName.newAreaCodes;
						for(i=newAreaCodes.length-1;i>=0;i=i-1)
						{
							var option = newAreaCodes.options[i];
							if(option.selected && i != (newAreaCodes.length-1))
							{
								swapOptions(newAreaCodes, i, i+1);
							}
							else if(option.selected)
							{
								option.selected = false;
							}
						}
					}
					var defaultString = " (Default)";
					*/
					
					function getSelectedValue(newAreaCodes)
					{
						var selectedValue = "";
						for(i=newAreaCodes.length-1;i>=0;i=i-1)
						{
							var option = newAreaCodes.options[i];
							if(option.selected)
							{
								//selectedValue = option.value;
								selectedValue = option;
								break;
							}
						}
						return selectedValue;
					} 
					
					function countSelectedItems()
					{
						var newAreaCodes = document.formName.selectedRoles;
						var count = 0;
						for(var i=newAreaCodes.length-1;i >= 0;i=i-1)
						{
							var option = newAreaCodes.options[i];
							if(option.selected)
								count++;
						}
						return count;
					}

