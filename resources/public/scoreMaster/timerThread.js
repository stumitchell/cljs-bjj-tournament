/*
1m = 60s
2m = 120s
3m = 180s
4m = 240s
5m = 300s
6m = 360s
7m = 420s
8m = 480s
10m = 600s
20m = 1200s

This code is based on code provided by James Edwards in his exploration of
creating accurate timers in javascript.
http://www.sitepoint.com/creating-accurate-timers-in-javascript/
*/

var start = new Date().getTime();
var time = 0;
var elapsed = '0.0';
var timeRemaining;

onmessage = function(e) {
    timeRemaining = e.data[0];

    function timerInstance() {
        time += 100;

        elapsed = Math.floor(time / 100) / 10;
        if (Math.round(elapsed) == elapsed) {
            elapsed += '.0';
            timeRemaining -= 1;
        }
        postMessage(timeRemaining);

        var diff = (new Date().getTime() - start) - time;
        if (timeRemaining > 0) {
            setTimeout(timerInstance, (100 - diff));
        } else {
            close();
        }
    }

    timerInstance();
};
