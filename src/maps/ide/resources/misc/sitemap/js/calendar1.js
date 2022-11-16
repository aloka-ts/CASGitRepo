// if two digit year input dates after this year considered 20 century.
var NUM_CENTYEAR = 30;
// is time input control required by default
var BUL_TIMECOMPONENT = false;
// are year scrolling buttons required by default
var BUL_YEARSCROLL = true;
// are Never Expires link not required by default
var NEVER_EXPIRES = false;
var NEVER_EXPIRES_STRING = "Never Expires";
var NEVER_EXPIRES_DATE = "01-JAN-2099 00:00:00";

var calendars = [];
var RE_NUM = /^\-?\d+$/;

function calendar1(obj_target) {

	// assing methods
	this.gen_date = cal_gen_date1;
	this.gen_time = cal_gen_time1;
	this.gen_tsmp = cal_gen_tsmp1;
	this.prs_date = cal_prs_date1;
	this.prs_time = cal_prs_time1;
	this.prs_tsmp = cal_prs_tsmp1;
	this.popup    = cal_popup1;

	// validate input parameters
	if (!obj_target)
		return cal_error("Error calling the calendar: no target control specified");
	if (obj_target.value == null)
		return cal_error("Error calling the calendar: parameter specified is not valid tardet control");
	this.target = obj_target;
	this.time_comp = BUL_TIMECOMPONENT;
	this.year_scroll = BUL_YEARSCROLL;
	this.expires_link = NEVER_EXPIRES;
	this.NEVER_EXPIRES_STRING = NEVER_EXPIRES_STRING;
	this.NEVER_EXPIRES_DATE = NEVER_EXPIRES_DATE;
	
	// register in global collections
	this.id = calendars.length;
	calendars[this.id] = this;
}

function cal_popup1 (str_datetime) {
	this.dt_current = this.prs_tsmp(str_datetime ? str_datetime : this.target.value);
	if (!this.dt_current) return;

	var obj_calwindow = window.open(
		'calendar.html?datetime=' + this.dt_current.valueOf()+ '&id=' + this.id,
		'Calendar', 'width=200,height='+(this.expires_link ? 235 : 215)+
		',status=no,resizable=no,top=200,left=200,dependent=yes,alwaysRaised=yes'
	);
	obj_calwindow.opener = window;
	obj_calwindow.focus();
}

// timestamp generating function
function cal_gen_tsmp1 (dt_datetime) {
	return(this.gen_date(dt_datetime) + ' ' + this.gen_time(dt_datetime));
}

// date generating function
function cal_gen_date1 (dt_datetime) {
	/*
	return (
		(dt_datetime.getDate() < 10 ? '0' : '') + dt_datetime.getDate() + "-"
		+ (dt_datetime.getMonth() < 9 ? '0' : '') + (dt_datetime.getMonth() + 1) + "-"
		+ dt_datetime.getFullYear()
	);
	*/
	return (
		(dt_datetime.getDate() < 10 ? '0' : '') + dt_datetime.getDate() + "-"
		+ getMonthName(dt_datetime) + "-" + dt_datetime.getFullYear()
	);
}
//prsd;; need to write a function to return the month name
function getMonthName(dt_datetime){
	return months[dt_datetime.getMonth()];
}

// time generating function
function cal_gen_time1 (dt_datetime) {
	return (
		(dt_datetime.getHours() < 10 ? '0' : '') + dt_datetime.getHours() + ":"
		+ (dt_datetime.getMinutes() < 10 ? '0' : '') + (dt_datetime.getMinutes()) + ":"
		+ (dt_datetime.getSeconds() < 10 ? '0' : '') + (dt_datetime.getSeconds())
	);
}

// timestamp parsing function
function cal_prs_tsmp1 (str_datetime) {
	// if no parameter specified return current timestamp
	if (!str_datetime)
		return (new Date());

	// if positive integer treat as milliseconds from epoch
	if (RE_NUM.exec(str_datetime))
		return new Date(str_datetime);
		
	//prasad
	if(str_datetime == NEVER_EXPIRES_STRING)
		return (new Date());

	// else treat as date in string format
	var arr_datetime = str_datetime.split(' ');
	return this.prs_time(arr_datetime[1], this.prs_date(arr_datetime[0]));
}

// date parsing function
function cal_prs_date1 (str_date) {

	var arr_date = str_date.split('-');

	if (arr_date.length != 3) return cal_error ("Invalid date format: '" + str_date + "'.\nFormat accepted is dd-mm-yyyy.");
	if (!arr_date[0]) return cal_error ("Invalid date format: '" + str_date + "'.\nNo day of month value can be found.");
	if (!RE_NUM.exec(arr_date[0])) return cal_error ("Invalid day of month value: '" + arr_date[0] + "'.\nAllowed values are unsigned integers.");
	if (!arr_date[1]) return cal_error ("Invalid date format: '" + str_date + "'.\nNo month value can be found.");
	//if (!RE_NUM.exec(arr_date[1])) return cal_error ("Invalid month value: '" + arr_date[1] + "'.\nAllowed values are unsigned integers.");
	if (!arr_date[2]) return cal_error ("Invalid date format: '" + str_date + "'.\nNo year value can be found.");
	if (!RE_NUM.exec(arr_date[2])) return cal_error ("Invalid year value: '" + arr_date[2] + "'.\nAllowed values are unsigned integers.");

	var dt_date = new Date();
	dt_date.setDate(1);

	
	//if (arr_date[1] < 1 || arr_date[1] > 12) return cal_error ("Invalid month value: '" + arr_date[1] + "'.\nAllowed range is 01-12.");
	//dt_date.setMonth(arr_date[1]-1);
	// the function to set the month from the month name
	var monthId =  getMonthId(arr_date[1]);
	if(monthId != -1)
		dt_date.setMonth(monthId);
	else
		return;//not a valid month
	//alert(dt_date)
	 
	if (arr_date[2] < 100) arr_date[2] = Number(arr_date[2]) + (arr_date[2] < NUM_CENTYEAR ? 2000 : 1900);
	dt_date.setFullYear(arr_date[2]);

	var dt_numdays = new Date(arr_date[2], arr_date[1], 0);
	dt_date.setDate(arr_date[0]);
	//if (dt_date.getMonth() != (arr_date[1]-1)) return cal_error ("Invalid day of month value: '" + arr_date[0] + "'.\nAllowed range is 01-"+dt_numdays.getDate()+".");

	return (dt_date)
}

//prasad
//to parse the "MON" format month
var months = new Array("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC");
function getMonthId(monthName){
	monthName = monthName.toUpperCase();
	var month = -1;
	for(var i=0;i < months.length;i=i+1){
		if(months[i] == monthName){
			month = i;
			break;
		}
	}
	if(month == -1){
		alert("Invalid Month Name. Please check the date format");
	}
	return month;
}

// time parsing function
function cal_prs_time1 (str_time, dt_date) {

	if (!dt_date) return null;
	var arr_time = String(str_time ? str_time : '').split(':');

	if (!arr_time[0]) dt_date.setHours(0);
	else if (RE_NUM.exec(arr_time[0])) 
		if (arr_time[0] < 24) dt_date.setHours(arr_time[0]);
		else return cal_error ("Invalid hours value: '" + arr_time[0] + "'.\nAllowed range is 00-23.");
	else return cal_error ("Invalid hours value: '" + arr_time[0] + "'.\nAllowed values are unsigned integers.");
	
	if (!arr_time[1]) dt_date.setMinutes(0);
	else if (RE_NUM.exec(arr_time[1]))
		if (arr_time[1] < 60) dt_date.setMinutes(arr_time[1]);
		else return cal_error ("Invalid minutes value: '" + arr_time[1] + "'.\nAllowed range is 00-59.");
	else return cal_error ("Invalid minutes value: '" + arr_time[1] + "'.\nAllowed values are unsigned integers.");

	if (!arr_time[2]) dt_date.setSeconds(0);
	else if (RE_NUM.exec(arr_time[2]))
		if (arr_time[2] < 60) dt_date.setSeconds(arr_time[2]);
		else return cal_error ("Invalid seconds value: '" + arr_time[2] + "'.\nAllowed range is 00-59.");
	else return cal_error ("Invalid seconds value: '" + arr_time[2] + "'.\nAllowed values are unsigned integers.");

	dt_date.setMilliseconds(0);
	return dt_date;
}

function cal_error (str_message) {
	alert (str_message);
	return null;
}

function calendar_disp(formname, elem){
	alert("hello " + elem + ' ' + formname);
	var date = document.getElementById(elem);
	alert(date);
	var cal = new calendar1(date);
	cal.year_scroll = true;
	cal.time_comp = true;
	cal.popup();
}

function calendar_disp_w_expires(elem){
	var cal = new calendar1(elem);
	cal.year_scroll = true;
	cal.time_comp = true;
	cal.expires_link = true;
	cal.popup();
}

function getJSDateFromDateString(datestr){
	//alert('getJSDateFromDateString');
	if(datestr == NEVER_EXPIRES_STRING)
		datestr = NEVER_EXPIRES_DATE;
	var tmp = datestr;
	while(tmp.indexOf('-') != -1){
		tmp = tmp.replace('-',' ');
	}
	return new Date(tmp);
}

function getDateStringFromJSDate(dateObj){
	//alert('getDateStringFromJSDate');
	//alert(dateObj)
	var date = dateObj.getDate();
	//var month = Calendar.get_month(dateObj.getMonth()).substr(0,3).toUpperCase();
	var month = months[dateObj.getMonth()];
	var year = dateObj.getFullYear();
	if(date < 10){
		date = "0" + date;
	}
	var hr = dateObj.getHours();
	if(hr < 10){
		hr = "0" + hr;
	}
	var min = dateObj.getMinutes();
	if(min < 10){
		min = "0" + min;
	}
	var sec = dateObj.getSeconds();
	if(sec < 10){
		sec = "0" + sec;
	}
	var dateStr = (date + "-" + month + "-" + year + " " + hr + ":" + min + ":" + sec);
	if(dateStr == NEVER_EXPIRES_DATE)//if date is 2099 jan 01; returns "never expires"
		dateStr = NEVER_EXPIRES_STRING;
	//alert('getDateStringFromJSDate end');
	return dateStr;
}

function returnCurrentDate(){
	return getDateStringFromJSDate(new Date());
}

function getLocalJSDate(gmtDate){
	//alert('getLocalJSDate');
	return new Date(gmtDate - gmtDate.getTimezoneOffset()*6e4);
}

function getGMTJSDate(localDate){
	//alert('getGMTJSDate '+localDate.getMonth());
	//alert(localDate)
	//alert("offset "+localDate.getTimezoneOffset()*6e4)
	//alert(localDate.getTimezoneOffset()*6e4)
	//alert(new Date(localDate - localDate.getTimezoneOffset()*6e4))
	var dt = new Date(localDate - (-localDate.getTimezoneOffset()*6e4));
	//alert("returning .... "+dt);
	return dt;
}

function getLocalDateString(gmtDateStr){
	//alert('getLocalDateString');
	return getDateStringFromJSDate(getLocalJSDate(getJSDateFromDateString(gmtDateStr)));
}

function getGMTDateString(localDateStr){
	//alert('getGMTDateString');
	return getDateStringFromJSDate(getGMTJSDate(getJSDateFromDateString(localDateStr)));
	//var t1 = getJSDateFromDateString(localDateStr);
	//alert("t1 = "+t1)
	//var t2 = getGMTJSDate(t1);
	//alert("t2 = "+t2)
	//var t3 = getDateStringFromJSDate(t2);
	//alert("t3 = "+t3)
	//return t3;
       }


function changeExpdate1(gmtDateHiddenFld, txtFld, expiryDateFlag)
{
         if(expiryDateFlag==false)
          {
            if(gmtDateHiddenFld.value==""){
               txtFld.value=returnCurrentDate();
             }
             else{
                txtFld.value=getLocalDateString(gmtDateHiddenFld.value);
              }
                 
          }
          else{
             if(expiryDateFlag==true)
             {
               if((gmtDateHiddenFld.value=="")|| (gmtDateHiddenFld.value==NEVER_EXPIRES_DATE)){
                      txtFld.value=NEVER_EXPIRES_STRING;
                }
                else
                 { 
                     txtFld.value=getLocalDateString(gmtDateHiddenFld.value);
                 }
             }
          }
            
}

function Expdate1(gmtDateHiddenFld, txtFld, expiryDateFlag)
{
          if(expiryDateFlag==false)
           {
            gmtDateHiddenFld.value=getGMTDateString(txtFld.value);
           }
          else
           {
              if(expiryDateFlag==true)
               {
                 if((txtFld.value=="") ||(txtFld.value==NEVER_EXPIRES_STRING))
                 {
                        gmtDateHiddenFld.value=NEVER_EXPIRES_DATE;
                 }
                 else
                  {
                      gmtDateHiddenFld.value=getGMTDateString(txtFld.value);
                   }
               }
           } 
}

function  getLocalDateStringLabel(countEl,dtLabel)
{
     for(var i=1;i<=countEl;i++)
     {
        var  localedate= document.getElementById(dtLabel+i);
        if(localedate==null){
            continue;
        }
        else if(localedate.innerHTML== NEVER_EXPIRES_STRING)
          {
               localedate.innerHTML = NEVER_EXPIRES_STRING;
           }
        else
         {
             localedate.innerHTML=getLocalDateString(localedate.innerHTML);
         }
     }
}
