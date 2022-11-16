// Title: Tigra Hints
// URL: http://www.softcomplex.com/products/tigra_hints/
// Version: 2.0
// Date: 02/22/2007
// Note: This script is free for any kind of applications
//	The development of this software is funded by your donations

function THints (a_items, a_cfg) {

	if (!a_items) a_items = [];
	if (!a_cfg) a_cfg = [];
	this.a_cfg = a_cfg;

	this.a_elements = [];
	this.a_hints = [];

	this.show  = f_hintShow;
	this.showD = f_hintShowNow;
	this.hide  = f_hintHide;
	this.hideD = f_hintHideNow;

	// register the object in global collection
	this.n_id = A_HINTS.length;
	A_HINTS[this.n_id] = this;
	
	if (!b_ie5 && !b_ie6)
		a_cfg.IEfix = false;

	// generate HTML
	for (var s_id in a_items) {
		s_id = String(s_id).replace(/\W/g,'');
		document.write(
			'<div style="position:absolute;left:0;top:0;visibility:hidden;z-index:',
			((a_cfg['z-index'] == null ? 2 : a_cfg['z-index']) + (a_cfg.IEfix ? 1 : 0)), ';',
			(a_cfg.IEtrans ? 'filter:' + a_cfg.IEtrans.join(' ') : '') ,
			(a_cfg.opacity ? ' alpha(opacity=' + a_cfg.opacity + '); -moz-opacity:' + (a_cfg.opacity / 100) + ';opacity:' + (a_cfg.opacity / 100) + '' : ''), '" id="h', this.n_id, '_', s_id,
			'" class="', (this.a_cfg.css ? this.a_cfg.css : 'tigraHint'),
			'" onmouseover="A_HINTS[', this.n_id + '].show(\'', s_id, '\')" onmouseout="A_HINTS[',
			this.n_id, '].hide(\'', s_id, '\')" onmousemove="f_onMouseMove(event)">',
			a_items[s_id], '</div>'
		);
		if (a_cfg.IEfix) document.write(
			'<iframe style="position:absolute;left:0;top:0;visibility:hidden;z-index:',
			(a_cfg['z-index'] == null ? 2 : a_cfg['z-index']), ';filter:alpha(opacity=0);" id="h',
			this.n_id, '_', s_id, '_if" frameborder="0" scrolling="No"></iframe>'
		);
	}

	// assign mouseover event	
	if (document.addEventListener) {
		document.addEventListener('mousemove', f_onMouseMove, true);
		window.addEventListener('scroll', f_onwindowChange, true);
		window.addEventListener('resize', f_onwindowChange, true);
	}
	else {
		document.onmousemove = f_onMouseMove;
		window.onscroll = f_onwindowChange;
		window.onresize = f_onwindowChange;
	}
}

function f_hintShow(s_id, e_element) {

	// cancel previous delay
	if (this.e_timer) {
		clearTimeout(this.e_timer);
		this.e_timer = null;
	}

	var s_id = String(s_id).replace(/\W/g,'');
	if (!this.a_hints[s_id])
		this.a_hints[s_id] = getElement('h' + this.n_id + '_' + s_id);
	if (!this.a_hints[s_id])
		this.a_hints[s_id] = getElement(s_id);
	if (!this.a_hints[s_id])
		throw new Error('001', 'Can not find the hint with ID=' + s_id);

	this.a_elements[s_id] = e_element;

	var n_showDelay = this.a_cfg.show_delay == null ? 200 : this.a_cfg.show_delay;
	if (!n_showDelay)
		return this.showD(s_id, e_element);

	this.e_timer = setTimeout('A_HINTS[' + this.n_id + '].showD("' + s_id + '")', n_showDelay);
}

function f_hintShowNow(s_id, e_element) {
	if (s_id == this.o_lastHintID)
		return;

	if (e_element)
		this.a_elements[s_id] = e_element;
	if (this.o_lastHintID != null)
		this.hideD(this.o_lastHintID);

	f_hintPosition(this.a_elements[s_id], this.a_hints[s_id], this.a_cfg);
	this.o_lastIframe = getElement('h' + this.n_id + '_' + s_id + '_if');
	if (this.o_lastIframe)
		this.o_lastIframe.style.visibility = 'visible';
	
	// Transition in IE
	if (this.a_cfg.IEtrans && this.a_cfg.IEtrans[0]) {
		try {
			var e_currTrans = this.a_hints[s_id].filters.item(0);
			e_currTrans.apply();
			this.a_hints[s_id].style.visibility = 'visible';
			e_currTrans.play();
		} catch(e) {
			this.a_hints[s_id].style.visibility = 'visible';
		};
	}
	else
		this.a_hints[s_id].style.visibility = 'visible';

	this.o_lastHintID = s_id;
}

function f_hintHide(s_id) {

	if (this.e_timer) {
		clearTimeout(this.e_timer);
		this.e_timer = null;
	}

	if (s_id != null)
		s_id = String(s_id).replace(/\W/g,'');
	else if (this.o_lastHintID)
		s_id = this.o_lastHintID;
	else
		return;
	
	if (!this.a_hints[s_id])
		throw new Error('001', 'Can not find the hint with ID=' + s_id);

	var n_hideDelay = this.a_cfg.hide_delay == null ? 200 : this.a_cfg.hide_delay;
	if (!n_hideDelay)
		return this.hideD(s_id);

	this.e_timer = setTimeout('A_HINTS[' + this.n_id + '].hideD("' + s_id + '")', n_hideDelay);
}

function f_hintHideNow(s_id) {
	// Transition in IE
	if (this.a_cfg.IEtrans && this.a_cfg.IEtrans[1]) {
		try {
			var e_currTrans = this.a_hints[s_id].filters.item(this.a_cfg.IEtrans[0] ? 1 : 0);
			e_currTrans.apply();
			this.a_hints[s_id].style.visibility = 'hidden';
			e_currTrans.play();
		} catch(e) {
			this.a_hints[s_id].style.visibility = 'hidden';
		};
	}
	else
		this.a_hints[s_id].style.visibility = 'hidden';

	this.o_lastHintID = null;
	if (this.o_lastIframe) {
		this.o_lastIframe.style.visibility = 'hidden';
		this.o_lastIframe = null;
	}
}


function f_hintPosition (e_element, e_hint, a_params) {
	// validate params
	if (!e_hint) throw new Error('001', 'hint object reference is missing in parameters');
	if (!a_params) a_params = [];
	
	var a_ = {
		n_elementWidth: e_element ? e_element.offsetWidth : 0,
		n_elementHeight: e_element ? e_element.offsetHeight : 0,
		n_elementLeft: e_element ? f_getPosition(e_element, 'Left') : n_mouseX,
		n_elementTop: e_element ? f_getPosition(e_element, 'Top') : n_mouseY,
		n_hintWidth: e_hint.offsetWidth,
		n_hintHeight: e_hint.offsetHeight,
		n_hintLeft: 0,
		n_hintTop : 0,
		n_clientWidth: f_clientWidth(),
		n_clientHeight: f_clientHeight(),
		n_scrollTop: f_scrollTop(),
		n_scrollLeft: f_scrollLeft(),
		s_align: a_params.align ? a_params.align : 'tlbl',
		n_gap: a_params.gap == null ? 5 : a_params.gap,
		n_margin: a_params.margin == null ? 10 : a_params.margin
	};

	f_applyAlign(a_);

	// smart positioning is on by default
	if (a_params.smart || a_params.smart == null)
		f_checkFit(a_);

	e_hint.style.left = a_.n_hintLeft + 'px';
	e_hint.style.top = a_.n_hintTop + 'px';

	// synchronize iframe if exists	
	var e_iframe = getElement(e_hint.id + '_if');
	if (e_iframe) {
		e_iframe.style.left = a_.n_hintLeft + 'px';
		e_iframe.style.top = a_.n_hintTop + 'px';
		e_iframe.style.width = a_.n_hintWidth + 'px';
		e_iframe.style.height = a_.n_hintHeight + 'px';
	}
}

/* corrects hints positioning to maximize the visibility */			
function f_checkFit (a_) {

	// check if correction is required
	if (a_.n_spaceT >= 0 && a_.n_spaceR >= 0 && a_.n_spaceB >= 0 && a_.n_spaceL >= 0)
		return;

	// determine if hint clears element for horisontal shifting
	var b_horShift =
		(a_.n_hintTop + a_.n_hintHeight + a_.n_gap <= a_.n_elementTop) ||
		(a_.n_elementTop + a_.n_elementHeight + a_.n_gap <= a_.n_hintTop);
	
	// resolve by horizontal shifting
	if (b_horShift) {
		if (a_.n_spaceL < 0 || (a_.n_spaceL + a_.n_spaceR < 0))
			a_.n_hintLeft = a_.n_scrollLeft + a_.n_margin;
		else if (a_.n_spaceR < 0)
			a_.n_hintLeft = a_.n_scrollLeft + a_.n_clientWidth - a_.n_margin - a_.n_hintWidth;
	}
	// determine if hint clears element for vertical shifting
	var b_verShift =
		(a_.n_hintLeft + a_.n_hintWidth + a_.n_gap <= a_.n_elementLeft) ||
		(a_.n_elementLeft + a_.n_elementWidth + a_.n_gap <= a_.n_hintLeft);

	// resolve by vertical shifting
	if (b_verShift) {
		if (a_.n_spaceT < 0 || (a_.n_spaceT + a_.n_spaceB < 0))
			a_.n_hintTop = a_.n_scrollTop + a_.n_margin;
		else if (a_.n_spaceB < 0)
			a_.n_hintTop = a_.n_scrollTop + a_.n_clientHeight - a_.n_margin - a_.n_hintHeight;
	}

	// resolve horisontal collision by mirroring
	if (!b_horShift && (a_.n_spaceL < 0 || a_.n_spaceR < 0)) {
		// save current overlap
		var n_overlap  = a_.n_spaceL + a_.n_spaceR,
			n_hintLeft = a_.n_hintLeft,
			n_hintTop  = a_.n_hintTop;

		// mirror the align
		a_.s_align = a_.s_align.replace('r', '-');
		a_.s_align = a_.s_align.replace('l', 'r');
		a_.s_align = a_.s_align.replace('-', 'l');
		f_applyAlign(a_);

		// restore old coordinate if mirrored hint is less visible
		if (Math.min(a_.n_spaceL, a_.n_spaceR) < n_overlap)
			a_.n_hintLeft = n_hintLeft;
		a_.n_hintTop = n_hintTop;
	}
	// resolve vertical collision by mirroring
	if (!b_verShift && (a_.n_spaceT < 0 || a_.n_spaceB < 0)) {
		var n_overlap  = Math.min(a_.n_spaceT, a_.n_spaceB),
			n_hintLeft = a_.n_hintLeft,
			n_hintTop  = a_.n_hintTop;

		// mirror the align
		a_.s_align = a_.s_align.replace('t', '-');
		a_.s_align = a_.s_align.replace('b', 't');
		a_.s_align = a_.s_align.replace('-', 'b');
		f_applyAlign(a_);

		// restore old coordinate if mirrored hint is less visible
		if (Math.min(a_.n_spaceT, a_.n_spaceB) < n_overlap)
			a_.n_hintTop = n_hintTop;
		a_.n_hintLeft = n_hintLeft;
	}
}

/* decodes the align parameter and calculates the coordinates of the hint */
function f_applyAlign (a_) {

	if (!re_align.exec(a_.s_align))
		throw new Error('001', 'Invalid format of align parameter: ' + a_.s_align);
	
	// decode alignment
	var n_align = RegExp.$1,
		n_top = a_.n_elementTop;

	// element vertical align
	if (n_align == 'm')
		n_top += Math.round(a_.n_elementHeight / 2);
	else if (n_align == 'b')
		n_top += a_.n_elementHeight + a_.n_gap;
	else
		n_top -= a_.n_gap;

	// hint vertical align
	n_align = RegExp.$3;
	if (n_align == 'm')
		n_top -= Math.round(a_.n_hintHeight / 2);
	else if (n_align == 'b')
		n_top -= a_.n_hintHeight;
		
	// element horizontal align
	var n_left = a_.n_elementLeft;
	n_align = RegExp.$2;
	if (n_align == 'c')
		n_left += Math.round(a_.n_elementWidth / 2);
	else if (n_align == 'r')
		n_left += a_.n_elementWidth + a_.n_gap;
	else
		n_left -= a_.n_gap;

	// hint horisontal align
	n_align = RegExp.$4;
	if (n_align == 'c')
		n_left -= Math.round(a_.n_hintWidth / 2);
	else if (n_align == 'r')
		n_left -= a_.n_hintWidth;

	a_.n_spaceT = n_top - a_.n_scrollTop - a_.n_margin,
	a_.n_spaceB = a_.n_clientHeight + a_.n_scrollTop - a_.n_margin - n_top - a_.n_hintHeight,
	a_.n_spaceL = n_left - a_.n_scrollLeft - a_.n_margin,
	a_.n_spaceR = a_.n_clientWidth + a_.n_scrollLeft - a_.n_margin - n_left - a_.n_hintWidth;

	a_.n_hintLeft = n_left;
	a_.n_hintTop  = n_top;
}

function f_onMouseMove(e_event) {
	if (!e_event && window.event)
		e_event = window.event;
	if (!e_event)
		return true;
	n_mouseX = e_event.pageX ? e_event.pageX : e_event.clientX + f_scrollLeft();
	n_mouseY = e_event.pageY ? e_event.pageY + 2 : e_event.clientY + f_scrollTop();
	return f_onwindowChange();
}

function f_onwindowChange() {
	var o_hint;
	for (var i = 0; i < A_HINTS.length; i++)
		o_hint = A_HINTS[i];
		if (o_hint.a_cfg.follow && o_hint.o_lastHintID)
			f_hintPosition(o_hint.a_elements[o_hint.o_lastHintID], o_hint.a_hints[o_hint.o_lastHintID], o_hint.a_cfg);

	return true;
}

/* browser abstraction layer */
function f_getPosition (e_elemRef, s_coord) {
	var n_pos = 0, n_offset,
		e_elem = e_elemRef;

	while (e_elem) {
		n_offset = e_elem["offset" + s_coord];
		n_pos += n_offset;
		e_elem = e_elem.offsetParent;
	}
	// margin correction in some browsers
	if (b_ieMac)
		n_pos += parseInt(document.body[s_coord.toLowerCase() + 'Margin']);
	else if (b_safari && (!this.o_block.b_relative || this.n_depth))
		n_pos -= n_offset;
	
	e_elem = e_elemRef;
	while (e_elem != document.body) {
		n_offset = e_elem["scroll" + s_coord];
		if (n_offset && e_elem.style.overflow == 'scroll')
			n_pos -= n_offset;
		e_elem = e_elem.parentNode;
	}
	return n_pos;
}

function f_clientWidth() {
	if (typeof(window.innerWidth) == 'number')
		return window.innerWidth;
	if (document.documentElement && document.documentElement.clientWidth)
		return document.documentElement.clientWidth;
	if (document.body && document.body.clientWidth)
		return document.body.clientWidth;
	return null;
}
function f_clientHeight() {
	if (typeof(window.innerHeight) == 'number')
		return window.innerHeight;
	if (document.documentElement && document.documentElement.clientHeight)
		return document.documentElement.clientHeight;
	if (document.body && document.body.clientHeight)
		return document.body.clientHeight;
	return null;
}
function f_scrollLeft() {
	if (typeof(window.pageXOffset) == 'number')
		return window.pageXOffset;
	if (document.body && document.body.scrollLeft)
		return document.body.scrollLeft;
	if (document.documentElement && document.documentElement.scrollLeft)
		return document.documentElement.scrollLeft;
	return 0;
}
function f_scrollTop() {
	if (typeof(window.pageYOffset) == 'number')
		return window.pageYOffset;
	if (document.body && document.body.scrollTop)
		return document.body.scrollTop;
	if (document.documentElement && document.documentElement.scrollTop)
		return document.documentElement.scrollTop;
	return 0;
}

getElement = document.all ?
	function (s_id) { return document.all[s_id] } :
	function (s_id) { return document.getElementById(s_id) };

// global variables
var A_HINTS = [],
	n_mouseX = 0,
	n_mouseY = 0,
	s_userAgent = navigator.userAgent.toLowerCase(),
	re_align = /^([tmb])([lcr])([tmb])([lcr])$/;

var b_mac = s_userAgent.indexOf('mac') != -1,
	b_ie5    = s_userAgent.indexOf('msie 5') != -1,
	b_ie6    = s_userAgent.indexOf('msie 6') != -1 && s_userAgent.indexOf('opera') == -1,
	b_ieMac  = b_mac && b_ie5,
	b_safari = b_mac && s_userAgent.indexOf('safari') != -1,
	b_opera6 = s_userAgent.indexOf('opera 6') != -1;

