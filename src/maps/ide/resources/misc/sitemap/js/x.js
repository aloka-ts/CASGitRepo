function move(from, to)
{
	var i = 0;
	for(i=from.length-1;i>=0;i=i-1)
	{
		var selected = from.options[i]; 
		if(selected.selected)
		{
			from.options[i] = null;
			var o = new Option(selected.text, selected.value, false, true);
			to.options[to.options.length] = o;
			alert (to.options[to.options.length-1].value);
			to.options[to.options.length-1].selected = true;
		}
	}
}

function moveRight(formname, available, selected)
{
	var ava = document.getElementById(formname + ':' + available);
	var sel = document.getElementById(formname + ':' + selected);
	move(ava , sel);
}

function moveLeft(formname, available, selected)
{
	var ava = document.getElementById(formname + ':' + available);
	var sel = document.getElementById(formname + ':' + selected);
	move(sel ,ava);
}

function selectAll(selectBox)
{
	for(i=0;i<selectBox.length;i=i+1) 
	{
		selectBox.options[i].selected = true;
	}
}

function moveAllRight(formname, available, selected)
{
	var ava = document.getElementById(formname + ':' + available);
	alert(ava);
	selectAll(ava);
	moveRight(formname, available, selected);
}
					
function moveAllLeft(formname, available, selected)
{
	
	var sel = document.getElementById(formname + ':' + selected);
	selectAll(sel);
	moveLeft(formname, available, selected);
}

function hello (object)
{
	alert ('hello: ' + object);
	alert (object.name);

	return true;
}