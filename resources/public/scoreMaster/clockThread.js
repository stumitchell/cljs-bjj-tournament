var i = '';

function timeNow() {
  var d = new Date(),
      h = (d.getHours() < 10 ? '0' : '') + d.getHours(),
      m = (d.getMinutes() < 10 ? '0' : '') + d.getMinutes();
  i = h + ':' + m; //i.value = h + ':' + m;
  postMessage(i);
  setTimeout('timeNow()', 1000);
}

timeNow();
