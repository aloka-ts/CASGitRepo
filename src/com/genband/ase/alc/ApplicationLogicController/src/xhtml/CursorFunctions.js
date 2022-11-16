function performAction(ss, id) 
{
    var instanceElement = document.getElementById(id);
    if (instanceElement!=null) 
    {
         var instance = instanceElement.parentNode.getInstanceDocument(id);
         if (instance!=null) 
         {
              var xslDom = instanceElement.parentNode.getInstanceDocument(ss);
              var processor = new XSLTProcessor()
              processor.importStylesheet(xslDom);

              var resultDom = processor.transformToDocument(instance, instance);

              instance.removeChild(instance.documentElement);
              instance.appendChild(resultDom.documentElement);

              instanceElement.parentNode.rebuild();
              instanceElement.parentNode.recalculate();
              instanceElement.parentNode.revalidate();
              instanceElement.parentNode.refresh();
         }
    }
}
function refresh(id) 
{
    var instanceElement = document.getElementById(id);
    if (instanceElement!=null) 
    {
         var instance = instanceElement.parentNode.getInstanceDocument(id);
         if (instance!=null) 
         {
              instanceElement.parentNode.rebuild();
              instanceElement.parentNode.recalculate();
              instanceElement.parentNode.revalidate();
              instanceElement.parentNode.refresh();
         }
    }
}
function insertCursor(id) 
{
    var instanceElement = document.getElementById(id);
    if (instanceElement!=null) 
    {
         var instance = instanceElement.parentNode.getInstanceDocument(id);
         if (instance!=null) 
         {
              var x=instance.getElementsByTagName('NodeDescriptor');
              for (i=0; i<x.length; i++)
              {
                   var currentNodeId = parseInt(x.item(i).getAttribute("nodeid"));
                   if (currentNodeId != i)
                   {
                        x.item(i).setAttribute("nodeid", "cursor");
                        break;
                   }
              }

              instanceElement.parentNode.rebuild();
              instanceElement.parentNode.recalculate();
              instanceElement.parentNode.revalidate();
              instanceElement.parentNode.refresh();
         }
    }
}
function doIt(hasHS)
{
  var colData = "";
  colData = "<font color='#0000FF' size='2' face='Arial'>";
  colData += "<b>If no, what is the highest grade attained: </B><br>";
  colData += "<SELECT name=noHSgradeLevel size=1><option selected>Select Grade Level</option>";
  colData += "<option>less than 8th Grade</option><option>9th Grade</option>";
  colData += "<option>10th Grade</option><option>11th Grade</option>";
  colData += "<option>12th Grade</option></SELECT></font>";

  if (document.layers)
  {
    document.layers['noHS'].document.open();
    document.layers['noHS'].document.write("<form name='noHS'>");
    document.layers['noHS'].document.write(colData);
    document.layers['noHS'].document.write("</form>");
    document.layers['noHS'].document.close();
    document.layers['noHS'].visibility = (hasHS) ? 'hide' : "show";
  }
  else
  {
    document.all['noHS'].style.visibility=(hasHS) ? 'hidden' :"visible";
    document.all.noHS.innerHTML = colData;
  }
}