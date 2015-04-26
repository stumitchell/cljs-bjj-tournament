// scoreMaster.js
// (c) David Scott
//
//document.getElementById("myH2").style.color = "#ff0000";var timer;
// acknowledge avgrund https://github.com/hakimel/avgrund/

var urlParams;
(window.onpopstate = function () {
    var match,
        pl     = /\+/g,  // Regex for replacing addition symbol with a space
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
        query  = window.location.search.substring(1);

    urlParams = {};
    while (match = search.exec(query))
       urlParams[decode(match[1])] = decode(match[2]);
})();


var version = 'version: Beta 17-APR 2015';
var matchDuration = 300; // 300 default; in seconds
var rollDuration = 120; // 120 default in seconds
var timeRemaining = matchDuration;
var rollRemaining = rollDuration;
var rest = 10; // default in seconds
var penalty = 'adult';
var p1Score = 0;
var p2Score = 0;
var p1Advantage = 0;
var p1Penalty = 0;
var p2Advantage = 0;
var p2Penalty = 0;
var p1ScoreFG = 'white';
var p1ScoreBG = 'blue';
var p2ScoreFG = 'white';
var p2ScoreBG = 'blue';
var timerFG = 'black';
var playerFG = 'black';
var timerBG = 'white';
var guiTheme = 'light';
var statusFG = 'black';
var statusBG = 'white';
var rollFG = 'black';
var statusMessage = 'READY!';
var p1AdvPenFG = 'black';
var p2AdvPenFG = 'black';
var timerActive = false;
var rolling = false;
var rollOrRest = 'roll';
var rollCount = 0;
var rollConfig = '';
var durationsIM = new Image();
var p1Name = urlParams['p1Name'] || 'Player 1';
var p1Team = urlParams['p1Team'] || 'myBJJ Team';
var p2Name = urlParams['p2Name'] || 'Player 2';
var p2Team = urlParams['p2Team'] || 'myBJJ Team';
var p1DefaultLogoPath = urlParams['p1DefaultLogoPath'] || 'resources/default_team_logo.png';
var p2DefaultLogoPath = urlParams['p2DefaultLogoPath'] || 'resources/default_team_logo.png';
var p1LogoIm = new Image();
var p2LogoIm = new Image();
var p1newLogoIM = new Image();
var p2newLogoIM = new Image();
var startBell = new Audio('resources/startBell.ogg');
var endBell = new Audio('resources/endBell.ogg');
var dingBell = new Audio('resources/ding.ogg');
var mode = 'scoring';

function init() {
	startClock();
	updateTimer();
	updateP1Score();
	updateP2Score();
	drawBranding();
	updateStatus(statusMessage);
	drawBelt();
	updateP1Adv();
	updateP1Pen();
	updateP2Adv();
	updateP2Pen();
	drawP1Controls();
	drawP2Controls();
	drawAdminControls();
	updateP1Name(p1Name);
	updateP1Team(p1Team);
	updateP2Name(p2Name);
	updateP2Team(p2Team);
	drawP1Logo(p1DefaultLogoPath);
	drawP2Logo(p2DefaultLogoPath);
	updateVersionNum();

	// document.addEventListener('DOMContentLoaded', function() {
	// 	document.getElementById('settingsBtn').addEventListener(
	// 		'click', function(e) {
	// 			Custombox.open({
	// 				target: '#modal',
	// 				effect: 'fadein'
	// 			});
	// 			e.preventDefault();
	// 		});
	// });

	document.getElementById('recalcBtn').disabled = true;
	
	window.addEventListener("keydown", spaceBarPressed, false);
}

function updateVersionNum() {
	document.getElementById('versionL').innerHTML = version;
}

function drawP1Logo(path) {
	p1LogoIm.src = path;
	var c = document.getElementById('p1_logoCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	p1LogoIm.onload = function() {
		ctx.drawImage(p1LogoIm, 0, 0);
	};
}

function updateP1Name(name) {
	if (name == '') {
		name = 'Player 1';
	}
	var c = document.getElementById('p1_nameCanvas');
	//var x = c.width;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = 'Bold 30px Arial';
	ctx.fillStyle = playerFG;
	//p1nameCtx.scale(1.1, 1.2);
	ctx.textAlign = 'left';
	ctx.fillText(name, 5, y + 10);
}

function updateP1Team(team) {
	if (team == '') {
		team = 'myBJJ Team';
	}
	var c = document.getElementById('p1_teamCanvas');
	//var x = c.width;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = 'Italic 18px Arial';
	ctx.fillStyle = playerFG;
	//p1teamCtx.scale(1.1, 1.2);
	ctx.textAlign = 'left';
	ctx.fillText(team, 5, y);
}

function drawP2Logo(path) {
	p2LogoIm.src = path;
	var c = document.getElementById('p2_logoCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	p2LogoIm.onload = function() {
		ctx.drawImage(p2LogoIm, 0, 0);
	};
}

function updateP2Name(name) {
	if (name == '') {
		name = 'Player 2';
	}
	var c = document.getElementById('p2_nameCanvas');
	var x = c.width;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = 'Bold 30px Arial';
	ctx.fillStyle = playerFG;
	//p2nameCtx.scale(1.1, 1.2);
	ctx.textAlign = 'right';
	ctx.fillText(name, x - 5, y + 10);
}

function updateP2Team(team) {
	if (team == '') {
		team = 'myBJJ Team';
	}
	var c = document.getElementById('p2_teamCanvas');
	var x = c.width;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = 'Italic 18px Arial';
	ctx.fillStyle = playerFG;
	//p2teamCtx.scale(1.1, 1.2);
	ctx.textAlign = 'right';
	ctx.fillText(team, x - 5, y);
}

function updateClock(time) {
	var c = document.getElementById('clockCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	//c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '50px Impact';
	ctx.fillStyle = '#666666';
	//clockCtx.scale(1.1, 1.2);
	ctx.textAlign = 'center';
	ctx.fillText(time, x, y + 20);
}

function drawBranding() {
	var brandingIm = new Image();
	if (guiTheme == 'light') {
		brandingIm.src = 'resources/branding_white.png';
	} else if (guiTheme == 'dark') {
		brandingIm.src = 'resources/branding_black.png';
	}
	var c = document.getElementById('brandingCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	brandingIm.onload = function() {
		ctx.drawImage(brandingIm, 0, 0);
	};
}

function drawBelt() {
	var beltIm = new Image();
	beltIm.src = 'resources/belt.png';
	var c = document.getElementById('beltCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	beltIm.onload = function() {
		ctx.drawImage(beltIm, 0, 0);
	};
}

function appendHistoryP1(entry) {
	var existing = document.getElementById('historyTextArea').value;
	var line = '@' + getRemaining(timeRemaining) + '\t' +
	entry + '>P1\tP1:' + p1Score + '\n' + existing;
	var existing = document.getElementById('historyTextArea').value = line;
}

function appendHistoryP2(entry) {
	var existing = document.getElementById('historyTextArea').value;
	var line = '@' + getRemaining(timeRemaining) + '\t' +
	entry + '>P2\tP2:' + p2Score + '\n' + existing;
	var existing = document.getElementById('historyTextArea').value = line;
}

function appendHistoryMSG(entry) {
	var existing = document.getElementById('historyTextArea').value;
	var line = '@' + getRemaining(timeRemaining) + '\t' +
	entry + '\n' + existing;
	var existing = document.getElementById('historyTextArea').value = line;
}

function p1Penalise(event) {
	if (event.button == 2) {
		p1Penalty -= 1;

		if (p1Penalty <= 0) {
			p1Penalty = 0;
		}

		updateP1Pen();
		appendHistoryMSG('-Pen>P1');

	} else if (event.button == 0) {
		p1Penalty += 1;
		updateP1Pen();

		if (penalty == 'adult') {
			if (p1Penalty > 4) {
				p1Penalty = 4;
				updateP1Pen();
			}
		} else {
			if (p1Penalty > 6) {
				p1Penalty = 6;
				updateP1Pen();
			}
		}

		if (p1Penalty == 1) {
			appendHistoryMSG('+Pen>P1');
		} else if (p1Penalty == 2) {
			p2Advantage += 1;
			updateP2Adv();
			appendHistoryMSG('+Pen>P1, +ADV>P2');
		} else if (p1Penalty == 3) {
			p2Score += 2;
			updateP2Score();
			appendHistoryMSG('+Pen>P1, +2pts>P2');
		} else if (p1Penalty == 4) {
			if (penalty == 'adult') {
				p1MatchEnd('pen');
			} else {
				p2Score += 2;
				updateP2Score();
				appendHistoryMSG('+Pen>P1, +2pts>P2');
			}
		} else if (p1Penalty == 5) {
			if (penalty == 'adult') {
				return;
			} else {
				p2Score += 2;
				updateP2Score();
				appendHistoryMSG('+Pen>P1, +2pts>P2');
			}
		} else if (p1Penalty == 6) {
			p1MatchEnd('pen');
		}
	}
}

function p2Penalise(event) {
	if (event.button == 2) {
		p2Penalty -= 1;

		if (p2Penalty <= 0) {
			p2Penalty = 0;
		}

		updateP2Pen();
		appendHistoryMSG('-Pen>P2');

	} else if (event.button == 0) {
		p2Penalty += 1;
		updateP2Pen();

		if (penalty == 'adult') {
			if (p2Penalty > 4) {
				p2Penalty = 4;
				updateP2Pen();
			}
		} else {
			if (p2Penalty > 6) {
				p2Penalty = 6;
				updateP2Pen();
			}
		}

		if (p2Penalty == 1) {
			appendHistoryMSG('+Pen>P2');
		} else if (p2Penalty == 2) {
			p1Advantage += 1;
			updateP1Adv();
			appendHistoryMSG('+Pen>P2, +ADV>P1');
		} else if (p2Penalty == 3) {
			p1Score += 2;
			updateP1Score();
			appendHistoryMSG('+Pen>P2, +2pts>P1');
		} else if (p2Penalty == 4) {
			if (penalty == 'adult') {
				p2MatchEnd('pen');
			} else {
				p1Score += 2;
				updateP1Score();
				appendHistoryMSG('+Pen>P2, +2pts>P1');
			}
		} else if (p2Penalty == 5) {
			if (penalty == 'adult') {
				return;
			} else {
				p1Score += 2;
				updateP1Score();
				appendHistoryMSG('+Pen>P2, +2pts>P1');
			}
		} else if (p2Penalty == 6) {
			p2MatchEnd('pen');
		}
	}
}

function p1Advantaged(event) {
	if (event.button == 0) {
		p1Advantage += 1;
		appendHistoryP1('+Adv');
	} else if (event.button == 2) {
		p1Advantage -= 1;
		appendHistoryP1('-Adv');
	}
	if (p1Advantage <= 0) {
		p1Advantage = 0;
	}
	updateP1Adv();
}

function p2Advantaged(event) {
	if (event.button == 0) {
		p2Advantage += 1;
		appendHistoryP2('+Adv');
	} else if (event.button == 2) {
		p2Advantage -= 1;
		appendHistoryP2('-Adv');
	}
	if (p2Advantage <= 0) {
		p2Advantage = 0;
	}
	updateP2Adv();
}

function p1OnePoint(event) {
	if (event.button == 0) {
		p1Score += 1;
		appendHistoryP1('+1');
	} else if (event.button == 2) {
		p1Score -= 1;
		appendHistoryP1('-1');
	}
	if (p1Score <= 0) {
		p1Score = 0;
	}
	updateP1Score();
}



function p1TwoPoints(event) {
	if (event.button == 0) {
		p1Score += 2;
		appendHistoryP1('+2');
	} else if (event.button == 2) {
		p1Score -= 2;
		appendHistoryP1('-2');
	}
	if (p1Score <= 0) {
		p1Score = 0;
	}
	updateP1Score();
}

function p1ThreePoints(event) {
	if (event.button == 0) {
		p1Score += 3;
		appendHistoryP1('+3');
	} else if (event.button == 2) {
		p1Score -= 3;
		appendHistoryP1('-3');
	}
	if (p1Score <= 0) {
		p1Score = 0;
	}
	updateP1Score();
}

function p1FourPoints(event) {
	if (event.button == 0) {
		p1Score += 4;
		appendHistoryP1('+4');
	} else if (event.button == 2) {
		p1Score -= 4;
		appendHistoryP1('-4');
	}
	if (p1Score <= 0) {
		p1Score = 0;
	}
	updateP1Score();
}

function p2OnePoint(event) {
	if (event.button == 0) {
		p2Score += 1;
		appendHistoryP2('+1');
	} else if (event.button == 2) {
		p2Score -= 1;
		appendHistoryP2('-1');
	}
	if (p2Score <= 0) {
		p2Score = 0;
	}
	updateP2Score();
}

function p2TwoPoints(event) {
	if (event.button == 0) {
		p2Score += 2;
		appendHistoryP2('+2');
	} else if (event.button == 2) {
		p2Score -= 2;
		appendHistoryP2('-2');
	}
	if (p2Score <= 0) {
		p2Score = 0;
	}
	updateP2Score();
}

function p2ThreePoints(event) {
	if (event.button == 0) {
		p2Score += 3;
		appendHistoryP2('+3');
	} else if (event.button == 2) {
		p2Score -= 3;
		appendHistoryP2('-3');
	}
	if (p2Score <= 0) {
		p2Score = 0;
	}
	updateP2Score();
}

function p2FourPoints(event) {
	if (event.button == 0) {
		p2Score += 4;
		appendHistoryP2('+4');
	} else if (event.button == 2) {
		p2Score -= 4;
		appendHistoryP2('-4');
	}
	if (p2Score <= 0) {
		p2Score = 0;
	}
	updateP2Score();
}

function updateP1Score() {
	var c = document.getElementById('p1scoreCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = p1ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '150px Impact';
	ctx.fillStyle = p1ScoreFG;
	ctx.textAlign = 'center';
	ctx.scale(1, 1.2);
	ctx.fillText(p1Score, x, y + 30);
}

function updateP1Adv() {
	var c = document.getElementById('p1AdvCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	//c.style.background = p1ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '30px Impact';
	ctx.fillStyle = 'white';
	ctx.textAlign = 'center';
	//p1AdvCtx.scale(1, 1.2);
	ctx.fillText(p1Advantage, x, y + 12);
}

function updateP1Pen() {
	var c = document.getElementById('p1PenCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	//c.style.background = p1ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '30px Impact';
	ctx.fillStyle = 'white';
	ctx.textAlign = 'center';
	//p1AdvCtx.scale(1, 1.2);
	ctx.fillText(p1Penalty, x, y + 12);
}

function updateP2Score() {
	var c = document.getElementById('p2scoreCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = p2ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '150px Impact';
	ctx.fillStyle = p2ScoreFG;
	ctx.textAlign = 'center';
	ctx.scale(1, 1.2);
	ctx.fillText(p2Score, x, y + 30);
}

function updateP2Adv() {
	var c = document.getElementById('p2AdvCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	//c.style.background = p1ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '30px Impact';
	ctx.fillStyle = 'white';
	ctx.textAlign = 'center';
	//p2AdvCtx.scale(1, 1.2);
	ctx.fillText(p2Advantage, x, y + 12);
}

function updateP2Pen() {
	var c = document.getElementById('p2PenCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	//c.style.background = p1ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '30px Impact';
	ctx.fillStyle = 'white';
	ctx.textAlign = 'center';
	//p2AdvCtx.scale(1, 1.2);
	ctx.fillText(p2Penalty, x, y + 12);
}

function updateStatus(newStatus) {
	if (newStatus == '') {
		newStatus = statusMessage;
	}

	var c = document.getElementById('statusCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '60px Impact';
	ctx.fillStyle = statusFG;
	ctx.textAlign = 'center';
	ctx.fillText(newStatus, x, y + 20);
}

function drawP1CrownCanvas() {
	var p1CrownIM = new Image();
	p1CrownIM.src = 'resources/crown.png';
	var c = document.getElementById('p1CrownCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	p1CrownIM.onload = function() {
		ctx.drawImage(p1CrownIM, 0, 0);
	};
	var hidePen =
	document.getElementById('p1PenL').style.visibility = 'hidden';
	var hideAdv =
	document.getElementById('p1AdvL').style.visibility = 'hidden';
}

function drawP2CrownCanvas() {
	var p2CrownIM = new Image();
	p2CrownIM.src = 'resources/crown.png';
	var c = document.getElementById('p2CrownCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	p2CrownIM.onload = function() {
		ctx.drawImage(p2CrownIM, 0, 0);
	};
	var hidePen =
	document.getElementById('p2PenL').style.visibility = 'hidden';
	var hideAdv =
	document.getElementById('p2AdvL').style.visibility = 'hidden';
}

function drawP1WinnerCanvas() {
	var c = document.getElementById('p1WinnerCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = p1ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '50px Impact';
	ctx.fillStyle = 'yellow';
	//statusCtx.scale(1.1, 1);
	ctx.textAlign = 'center';
	ctx.fillText('WINNER', x, y + 20);
}

function drawP2WinnerCanvas() {
	var c = document.getElementById('p2WinnerCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = p2ScoreBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '50px Impact';
	ctx.fillStyle = 'yellow';
	//statusCtx.scale(1.1, 1);
	ctx.textAlign = 'center';
	ctx.fillText('WINNER', x, y + 20);
}

function updateTimer() {
	var c = document.getElementById('timerCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	//c.width = c.width;
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '240px Digital-7 Mono';
	ctx.fillStyle = timerFG;
	ctx.scale(1.1, 1.2);
	ctx.textAlign = 'center';
	ctx.fillText(getRemaining(timeRemaining), x - 30, y + 60);
}

function toggleTimer() {
	if (timerActive) {
		timerActive = false;
		stopTimer();
		document.getElementById('settingsBtn').disabled = false;
		document.getElementById('resetBtn').disabled = false;
		document.getElementById('recalcBtn').disabled = true;
	} else {
		timerActive = true;
		startTimer();
		document.getElementById('settingsBtn').disabled = true;
		document.getElementById('resetBtn').disabled = true;
		document.getElementById('recalcBtn').disabled = true;
	}
	dingBell.play();
}

function spaceBarPressed(e) {
	if (mode == 'scoring') {
		if (e.keyCode == "32") {
			toggleTimer();
		}
	} else if (mode == 'rolling') {
		if (e.keyCode == "32") {
			toggleRoll();
		}
	}
}

function startTimer() {
	timerFG = 'green';
	statusFG = 'green';
	statusMessage = 'VERSUS!';
	updateStatus(statusMessage);

	if (typeof (Worker) !== 'undefined') {
		if (typeof (timer) == 'undefined') {
			timer = new Worker('timerThread.js');
			timer.postMessage([timeRemaining]);
		}

		timer.onmessage = function(event) {
			timeRemaining = event.data;
			updateTimer();

			if (timeRemaining <= 0) {
				gameOver();
			}
		};


	} else {
		document.getElementById('timerDisplay').innerHTML =
		'Sorry, your browser does not support Web Workers...';
	}
}

function gameOver() {
	endBell.play();
	timerFG = 'red';
	statusFG = 'red';
	updateTimer();

	statusMessage = 'GAME OVER!';
	updateStatus(statusMessage);

	getWinner();

	document.getElementById('settingsBtn').disabled = false;
	document.getElementById('resetBtn').disabled = false;
	document.getElementById('recalcBtn').disabled = false;

	if (typeof(timer) != 'undefined') {
		timer.terminate();
		timer = undefined;
	}

	timerActive = false;

}

function p1MatchEnd(code) {
	endBell.play();
	timerFG = 'red';
	statusFG = 'red';
	updateTimer();

	if (code == 'sub') {
		statusMessage = 'SUBMISSION WIN!';
		updateStatus(statusMessage);
		crownP1();
		appendHistoryMSG('P1 Submission Win');
	} else if (code == 'dq') {
		statusMessage = 'DISQUALIFICATION!';
		updateStatus(statusMessage);
		crownP2();
		appendHistoryMSG('P1 Disqualified');
	} else if (code == 'pen') {
		statusMessage = 'PENALTY LOSS!';
		updateStatus(statusMessage);
		crownP2();
		appendHistoryMSG('P1 Penality Loss');
	} else if (code == 'adv') {
		statusMessage = 'ADVANTAGE WIN!';
		updateStatus(statusMessage);
		crownP1();
		appendHistoryMSG('P1 Advantage Win');
	} else if (code == 'pts') {
		statusMessage = 'POINTS WIN!';
		updateStatus(statusMessage);
		crownP1();
		appendHistoryMSG('P1 Points Win');
	} else if (code == 'win') {
		statusMessage = 'GAME OVER!';
		updateStatus(statusMessage);
		crownP1();
		appendHistoryMSG('P1 is Winner');
	}

	if (typeof(timer) != 'undefined') {
		timer.terminate();
		timer = undefined;
	}

	timerActive = false;
	document.getElementById('recalcBtn').disabled = false;
	document.getElementById('settingsBtn').disabled = false;
	document.getElementById('resetBtn').disabled = false;
}

function crownP1() {
	drawP1CrownCanvas();
	drawP1WinnerCanvas();
	drawP2CrownCanvas();
	drawP2WinnerCanvas();
	document.getElementById('p1CrownCanvas').style.visibility = 'visible';
	document.getElementById('p1WinnerCanvas').style.visibility = 'visible';
	document.getElementById('p2CrownCanvas').style.visibility = 'hidden';
	document.getElementById('p2WinnerCanvas').style.visibility = 'hidden';
	document.getElementById('p1AdvL').style.visibility = 'hidden';
	document.getElementById('p1PenL').style.visibility = 'hidden';
	document.getElementById('p2AdvL').style.visibility = 'visible';
	document.getElementById('p2PenL').style.visibility = 'visible';
}

function crownP2() {
	drawP1CrownCanvas();
	drawP1WinnerCanvas();
	drawP2CrownCanvas();
	drawP2WinnerCanvas();
	document.getElementById('p1CrownCanvas').style.visibility = 'hidden';
	document.getElementById('p1WinnerCanvas').style.visibility = 'hidden';
	document.getElementById('p2CrownCanvas').style.visibility = 'visible';
	document.getElementById('p2WinnerCanvas').style.visibility = 'visible';
	document.getElementById('p1AdvL').style.visibility = 'visible';
	document.getElementById('p1PenL').style.visibility = 'visible';
	document.getElementById('p2AdvL').style.visibility = 'hidden';
	document.getElementById('p2PenL').style.visibility = 'hidden';
}

function p2MatchEnd(code) {
	endBell.play();
	timerFG = 'red';
	statusFG = 'red';
	updateTimer();

	if (code == 'sub') {
		statusMessage = 'SUBMISSION WIN!';
		updateStatus(statusMessage);
		crownP2();
		appendHistoryMSG('P2 Submission Win');
	} else if (code == 'dq') {
		statusMessage = 'DISQUALIFICATION!';
		updateStatus(statusMessage);
		crownP1();
		appendHistoryMSG('P2 Disqualified');
	} else if (code == 'pen') {
		statusMessage = 'PENALTY LOSS!';
		updateStatus(statusMessage);
		crownP1();
		appendHistoryMSG('P2 Penalty Loss');
	} else if (code == 'adv') {
		statusMessage = 'ADVANTAGE WIN!';
		updateStatus(statusMessage);
		crownP2();
		appendHistoryMSG('P2 Advantage Win');
	} else if (code == 'pts') {
		statusMessage = 'POINTS WIN!';
		updateStatus(statusMessage);
		crownP2();
		appendHistoryMSG('P2 Points Win');
	} else if (code == 'win') {
		statusMessage = 'GAME OVER!';
		updateStatus(statusMessage);
		crownP2();
		appendHistoryMSG('P2 is Winner');
	}

	if (typeof(timer) != 'undefined') {
		timer.terminate();
		timer = undefined;
	}

	timerActive = false;
	document.getElementById('recalcBtn').disabled = false;
	document.getElementById('settingsBtn').disabled = false;
	document.getElementById('resetBtn').disabled = false;
}

function stopTimer() {
	timerFG = 'red';
	statusFG = 'red';
	updateTimer();
	statusMessage = 'TIME OUT!';
	updateStatus(statusMessage);

	timer.terminate();
	timer = undefined;
}

function resetTimer() {
	if (typeof(timer) !== 'undefined') {
		return;
	} else {
		timerActive = false;
		timeRemaining = matchDuration;
		mode = 'scoring';

		if (guiTheme == 'light') {
			timerFG = 'black';
			timerBG = 'white';
			statusFG = 'black';
			statusBG = 'white';
		} else if (guiTheme == 'dark') {
			timerFG = 'white';
			timerBG = 'black';
			statusFG = 'white';
			statusBG = 'black';
		}

		p1Score = 0;
		p2Score = 0;
		p1Advantage = 0;
		p2Advantage = 0;
		p1Penalty = 0;
		p2Penalty = 0;

		updateP1Adv();
		updateP2Adv();
		updateP1Pen();
		updateP2Pen();
		updateP1Score();
		updateP2Score();

		statusMessage = 'READY!';
		updateStatus(statusMessage);
		updateTimer();
		document.getElementById('p1CrownCanvas').style.visibility = 'hidden';
		document.getElementById('p1WinnerCanvas').style.visibility = 'hidden';
		document.getElementById('p2CrownCanvas').style.visibility = 'hidden';
		document.getElementById('p2WinnerCanvas').style.visibility = 'hidden';
		document.getElementById('p1AdvL').style.visibility = 'visible';
		document.getElementById('p1PenL').style.visibility = 'visible';
		document.getElementById('p2AdvL').style.visibility = 'visible';
		document.getElementById('p2PenL').style.visibility = 'visible';
		document.getElementById('historyTextArea').value = '';
		document.getElementById('recalcBtn').disabled = true;
	}
}

function recalcWinner() {
		//statusMessage = 'READY!';
		//updateStatus(statusMessage);
		//updateTimer();
		document.getElementById('p1CrownCanvas').style.visibility = 'hidden';
		document.getElementById('p1WinnerCanvas').style.visibility = 'hidden';
		document.getElementById('p2CrownCanvas').style.visibility = 'hidden';
		document.getElementById('p2WinnerCanvas').style.visibility = 'hidden';
		getWinner();
	}

	function getRemaining(seconds) {
		var timeLeft = '';
		var minutes;

		minutes = Math.floor(seconds / 60);
		seconds = seconds - minutes * 60;

		if (minutes < 10) {
			timeLeft = '0' + minutes;
		} else {
			timeLeft = minutes;
		}
		if (seconds < 10) {
			timeLeft += ':0' + seconds;
		} else {
			timeLeft += ':' + seconds;
		}
		return timeLeft;
	}

	function startClock() {
		if (typeof(Worker) !== 'undefined') {
			if (typeof(clock) == 'undefined') {
				clock = new Worker('clockThread.js');
			}
			clock.onmessage = function(event) {
				updateClock(event.data);
			};
		} else {
			document.getElementById('clockDisplay').innerHTML =
			'Sorry, your browser does not support Web Workers...';
		}
	}

	function drawP1Controls() {
		drawP1_2pts();
		drawP1_3pts();
		drawP1_4pts();
		drawP1_Adv();
		drawP1_Pen();
		drawP1_DQ();
		drawP1_Sub();
		drawP1_SetWin();
	}

	function drawP1_2pts() {
		var p1_2ptsIm = new Image();
		p1_2ptsIm.src = 'resources/button_2pts1.png';
		var c = document.getElementById('p1_2ptsCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_2ptsIm.onload = function() {
			ctx.drawImage(p1_2ptsIm, 0, 0);
		};
	}

	function drawP1_3pts() {
		var p1_3ptsIm = new Image();
		p1_3ptsIm.src = 'resources/button_3pts1.png';
		var c = document.getElementById('p1_3ptsCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_3ptsIm.onload = function() {
			ctx.drawImage(p1_3ptsIm, 0, 0);
		};
	}

	function drawP1_4pts() {
		var p1_4ptsIm = new Image();
		p1_4ptsIm.src = 'resources/button_4pts1.png';
		var c = document.getElementById('p1_4ptsCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_4ptsIm.onload = function() {
			ctx.drawImage(p1_4ptsIm, 0, 0);
		};
	}

	function drawP1_Adv() {
		var p1_advIm = new Image();
		p1_advIm.src = 'resources/button_advantage1.png';
		var c = document.getElementById('p1_AdvCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_advIm.onload = function() {
			ctx.drawImage(p1_advIm, 0, 0);
		};
	}

	function drawP1_Pen() {
		var p1_penIm = new Image();
		p1_penIm.src = 'resources/button_penalty1.png';
		var c = document.getElementById('p1_PenCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_penIm.onload = function() {
			ctx.drawImage(p1_penIm, 0, 0);
		};
	}

	function drawP1_DQ() {
		var p1_dqIm = new Image();
		p1_dqIm.src = 'resources/button_disqualify1.png';
		var c = document.getElementById('p1_DQCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_dqIm.onload = function() {
			ctx.drawImage(p1_dqIm, 0, 0);
		};
	}

	function drawP1_Sub() {
		var p1_subIm = new Image();
		p1_subIm.src = 'resources/button_winBySub1.png';
		var c = document.getElementById('p1_SubCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_subIm.onload = function() {
			ctx.drawImage(p1_subIm, 0, 0);
		};
	}

	function drawP1_SetWin() {
		var p1_subIm = new Image();
		p1_subIm.src = 'resources/button_setWinner1.png';
		var c = document.getElementById('p1_SetWinnerCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_subIm.onload = function() {
			ctx.drawImage(p1_subIm, 0, 0);
		};
	}

	function drawP2Controls() {
		drawP2_2pts();
		drawP2_3pts();
		drawP2_4pts();
		drawP2_Adv();
		drawP2_Pen();
		drawP2_DQ();
		drawP2_Sub();
		drawP2_SetWin();
	}

	function drawP2_2pts() {
		var p2_2ptsIm = new Image();
		p2_2ptsIm.src = 'resources/button_2pts2.png';
		var c = document.getElementById('p2_2ptsCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p2_2ptsIm.onload = function() {
			ctx.drawImage(p2_2ptsIm, 0, 0);
		};
	}

	function drawP2_3pts() {
		var p2_3ptsIm = new Image();
		p2_3ptsIm.src = 'resources/button_3pts2.png';
		var c = document.getElementById('p2_3ptsCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p2_3ptsIm.onload = function() {
			ctx.drawImage(p2_3ptsIm, 0, 0);
		};
	}

	function drawP2_4pts() {
		var p2_4ptsIm = new Image();
		p2_4ptsIm.src = 'resources/button_4pts2.png';
		var c = document.getElementById('p2_4ptsCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p2_4ptsIm.onload = function() {
			ctx.drawImage(p2_4ptsIm, 0, 0);
		};
	}

	function drawP2_Adv() {
		var p2_advIm = new Image();
		p2_advIm.src = 'resources/button_advantage2.png';
		var c = document.getElementById('p2_AdvCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p2_advIm.onload = function() {
			ctx.drawImage(p2_advIm, 0, 0);
		};
	}

	function drawP2_Pen() {
		var p2_penIm = new Image();
		p2_penIm.src = 'resources/button_penalty2.png';
		var c = document.getElementById('p2_PenCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p2_penIm.onload = function() {
			ctx.drawImage(p2_penIm, 0, 0);
		};
	}

	function drawP2_DQ() {
		var p2_dqIm = new Image();
		p2_dqIm.src = 'resources/button_disqualify2.png';
		var c = document.getElementById('p2_DQCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p2_dqIm.onload = function() {
			ctx.drawImage(p2_dqIm, 0, 0);
		};
	}
	function drawP2_Sub() {
		var p2_subIm = new Image();
		p2_subIm.src = 'resources/button_winBySub2.png';
		var c = document.getElementById('p2_SubCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p2_subIm.onload = function() {
			ctx.drawImage(p2_subIm, 0, 0);
		};
	}

	function drawP2_SetWin() {
		var p1_subIm = new Image();
		p1_subIm.src = 'resources/button_setWinner2.png';
		var c = document.getElementById('p2_SetWinnerCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		p1_subIm.onload = function() {
			ctx.drawImage(p1_subIm, 0, 0);
		};
	}

	function drawAdminControls() {
		drawTimeOut();
		drawContRoll();
	}

	function drawTimeOut() {
		var timeOutIm = new Image();
		timeOutIm.src = 'resources/button_timeOut.png';
		var c = document.getElementById('timeOutCanvas');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		timeOutIm.onload = function() {
			ctx.drawImage(timeOutIm, 0, 0);
		};
	}

	function drawContRoll() {
		var contRollIm = new Image();
		contRollIm.src = 'resources/button_contRoll.png';
		var c = document.getElementById('contRollCanvasBtn');
		var ctx = c.getContext('2d');
		ctx.setTransform(1, 0, 0, 1, 0, 0);
		ctx.clearRect(0, 0, c.width, c.height);
		contRollIm.onload = function() {
			ctx.drawImage(contRollIm, 0, 0);
		};
	}

// function settingsPopUp() {
// 	el = document.getElementById('popup');
// 	el.style.visibility =
// 	(el.style.visibility == 'visible') ? 'hidden' : 'visible';
// 	fillSettingDefaults();
// }

function getP1Logo() {
	var c = document.getElementById('p1newLogoCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	var d = document.getElementById('p1_logoCanvas');
	ctx.drawImage(d, 0, 0);
}

function getP2Logo() {
	var c = document.getElementById('p2newLogoCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	var d = document.getElementById('p2_logoCanvas');
	ctx.drawImage(d, 0, 0);
}

function fillSettingDefaults() {
	//selectP1Logo(p1DefaultLogoPath);
	//selectP2Logo(p2DefaultLogoPath);

	getP1Logo();
	getP2Logo();

	// p1 new logo loader
	var imageLoader1 = document.getElementById('p1newLogo');
	imageLoader1.addEventListener('change', loadNewP1Logo, false);

	var c = document.getElementById('p1newLogoCanvas');
	var ctx = c.getContext('2d');

	function loadNewP1Logo(e) {
		var reader = new FileReader();

		reader.onload = function(event) {
			var img = new Image();
			img.onload = function() {
				c.width = img.width;
				c.height = img.height;
				ctx.drawImage(img, 0, 0, img.width, img.height,
					0, 0, 70, 70);
			};
			img.src = event.target.result;
		};
		reader.readAsDataURL(e.target.files[0]);
	}

	// p2 new logo loader
	var imageLoader2 = document.getElementById('p2newLogo');
	imageLoader2.addEventListener('change', loadNewP2Logo, false);

	var d = document.getElementById('p2newLogoCanvas');
	var dtx = d.getContext('2d');

	function loadNewP2Logo(e) {
		var reader = new FileReader();

		reader.onload = function(event) {
			var img = new Image();
			img.onload = function() {
				d.width = img.width;
				d.height = img.height;
				dtx.drawImage(img, 0, 0, img.width, img.height,
					0, 0, 70, 70);
			};
			img.src = event.target.result;
		};
		reader.readAsDataURL(e.target.files[0]);
	}
}

function saveSettings() {
	// el = document.getElementById('popup');
	// el.style.visibility =
	// (el.style.visibility == 'visible') ? 'hidden' : 'visible';

	guiTheme = document.querySelector('input[name="theme"]:checked').value;
	matchDuration = document.querySelector(
		'input[name="matchDuration"]:checked').value;
	timeRemaining = matchDuration;
	penalty = document.querySelector(
		'input[name="penaltyMode"]:checked').value;

	p1GiColour = document.querySelector(
		'input[name="p1giCol"]:checked').value;
	if (p1GiColour == 'white') {
		p1ScoreFG = 'black';
		p1ScoreBG = 'white';
		document.getElementById(
			'p1scoreCanvas').style.border = 'black 1px solid';
	} else if (p1GiColour == 'blue') {
		p1ScoreFG = 'white';
		p1ScoreBG = 'blue';
	} else if (p1GiColour == 'black') {
		p1ScoreFG = 'white';
		p1ScoreBG = 'black';
		document.getElementById(
			'p1scoreCanvas').style.border = 'white 1px solid';
	}

	p2GiColour = document.querySelector(
		'input[name="p2giCol"]:checked').value;
	if (p2GiColour == 'white') {
		p2ScoreFG = 'black';
		p2ScoreBG = 'white';
		document.getElementById(
			'p1scoreCanvas').style.border = 'black 1px solid';
	} else if (p2GiColour == 'blue') {
		p2ScoreFG = 'white';
		p2ScoreBG = 'blue';
	} else if (p2GiColour == 'black') {
		p2ScoreFG = 'white';
		p2ScoreBG = 'black';
		document.getElementById(
			'p2scoreCanvas').style.border = 'white 1px solid';
	}

	p1Name = document.getElementById('p1NameF').value;
	p2Name = document.getElementById('p2NameF').value;
	p1Team = document.getElementById('p1TeamF').value;
	p2Team = document.getElementById('p2TeamF').value;

	updateGuiFields();
}

function updateGuiFields() {
	updateTimer();
	updateP1Score();
	updateP2Score();
	updateP1Name(p1Name);
	updateP2Name(p2Name);
	updateP1Team(p1Team);
	updateP2Team(p2Team);
	updateTheme(guiTheme);
	updateP1Logo();
	updateP2Logo();
	resetTimer();
}

function updateP1Logo() {
	var c = document.getElementById('p1_logoCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	var d = document.getElementById('p1newLogoCanvas');
	ctx.drawImage(d, 0, 0);
}

function updateP2Logo() {
	var c = document.getElementById('p2_logoCanvas');
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	var d = document.getElementById('p2newLogoCanvas');
	ctx.drawImage(d, 0, 0);
}

function updateTheme(colour) {
	if (colour == 'light') {
		document.body.style.backgroundColor = 'white';
		timerFG = 'black';
		playerFG = 'black';
		timerBG = 'white';
		statusFG = 'black';
		statusBG = 'white';
		rollFG = 'black';
	} else if (colour == 'dark') {
		document.body.style.backgroundColor = 'black';
		timerFG = 'white';
		playerFG = 'white';
		timerBG = 'black';
		statusFG = 'white';
		statusBG = 'black';
		rollFG = 'white';
	}

	updateP1Name(p1Name);
	updateP2Name(p2Name);
	updateP1Team(p1Team);
	updateP2Team(p2Team);
	drawBranding();
	updateTimer();
	updateStatus();
}

function contRollDialog() {
	window.location.href = '#contRollModal';

	if (typeof(timer) != 'undefined') {
		timer.terminate();
		timer = undefined;
	}
	timerActive = false;

	mode = 'rolling';
	updateRollSettings();
	updateTheme();
	updateRollTimer();
	updateRollCount();
	updateRollStatus('READY!');
	rollOrRest = 'roll';

	startRollClock();

	document.getElementById('updateRollBtn').disabled = false;

	var restElements = document.restOptions.restTime;	
	for (var i = 0, iLen = restElements.length; i < iLen; i++) {
		restElements[i].disabled = false;
	}

	var durationElements = document.rollDurationOptions.rollTime;
	for (var i = 0, iLen = durationElements.length; i < iLen; i++) {
		durationElements[i].disabled = false;
	}
}

function exitContRoll() {
	//dingBell.play();

	if (typeof (rollTimer) != 'undefined') {
		rollTimer.terminate();
	}

	rollTimer = undefined;
	rollRemaining = rollDuration;
	rolling = false;

	resetTimer();
	// if (guiTheme == 'light') {
	// 	rollFG = 'black';
	// } else if (guiTheme == 'dark') {
	// 	rollFG = 'white';
	// }

	// timerActive = false;
	// timeRemaining = matchDuration
	// updateTimer();
	// updateStatus('READY!');

	mode = 'scoring';
}

function updateRollTimer() {
	var c = document.getElementById('rollingCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '450px Digital-7 Mono';
	ctx.fillStyle = rollFG;
	ctx.scale(1.1, 1.2);
	ctx.textAlign = 'center';
	ctx.fillText(getRemaining(rollRemaining), x - 55, y + 70);
}

function updateRollCount() {
	var c = document.getElementById('rollCountCanvas');
	var x = 0;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '40px Liberation Sans Narrow';
	ctx.fillStyle = 'grey';
	//ctx.textAlign = 'left';
	ctx.fillText('Round: ' + rollCount, x, y - 5);
	ctx.fillText(rollConfig, x, y + 35);
}

function updateRollStatus(newStatus) {
	if (newStatus == '') {
		newStatus = statusMessage;
	}
	var c = document.getElementById('rollStatusCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '90px Impact';
	ctx.fillStyle = rollFG;
	ctx.textAlign = 'center';
	ctx.fillText(newStatus, x, y + 35);
}

function toggleRoll() {
	if (rollRemaining == 0) {
		// do nothing
	} else if (rolling) {
		rolling = false;
		stopRoll();
		dingBell.play();
	} else {
		rolling = true;
		startRoll();
	}
}

function startRoll() {
	document.getElementById('updateRollBtn').disabled = true;

	var restElements = document.restOptions.restTime;
	for (var i = 0, iLen = restElements.length; i < iLen; i++) {
		restElements[i].disabled = true;
	}
	var durationElements = document.rollDurationOptions.rollTime;
	for (var i = 0, iLen = durationElements.length; i < iLen; i++) {
		durationElements[i].disabled = true;
	}

	if (rollOrRest == 'roll') {
		rollFG = 'green';
		statusMessage = 'VERSUS!';
		updateRollStatus(statusMessage);

		if (rollRemaining == rollDuration) {
			startBell.play();
			rollCount ++;
			updateRollCount();
		} else {
			dingBell.play();
		}
	} else if (rollOrRest == 'rest') {
		dingBell.play();
		rollFG = 'grey';
		updateRollTimer();
		statusMessage = 'REST TIME!';
		updateRollStatus(statusMessage);
	}

	if (typeof (Worker) !== 'undefined') {
		if (typeof (rollTimer) == 'undefined') {
			rollTimer = new Worker('timerThread.js');
			rollTimer.postMessage([rollRemaining]);
		}

		rollTimer.onmessage = function(event) {
			rollRemaining = event.data;
			updateRollTimer();

			if (document.querySelector('input[name="restTime"]:checked').value == 0) {
				singleRoll = true;
			} else {
				singleRoll = false;
			}

			if (rollRemaining <= 0) {
				if (singleRoll) {
					endSingleRoll();
				} else {
					flip();
				}
			}
		};
	}
}

function endSingleRoll() {
	endBell.play();
	rolling = false;
	rollOrRest = 'roll';
	rollCount = 0;

	document.getElementById('updateRollBtn').disabled = false;

	if (typeof (rollTimer) != 'undefined') {
		rollTimer.terminate();
	}
	rollTimer = undefined;
	rolling = true;
	rollFG = 'red';
	updateRollTimer();
	statusMessage = 'TIME OVER!';
	updateRollStatus(statusMessage);

	var restElements = document.restOptions.restTime;
	for (var i = 0, iLen = restElements.length; i < iLen; i++) {
		restElements[i].disabled = false;
	}

	var durationElements = document.rollDurationOptions.rollTime;
	for (var i = 0, iLen = durationElements.length; i < iLen; i++) {
		durationElements[i].disabled = false;
	}
}

function flip() {
	if (rollOrRest == 'rest') {
		rollOrRest = 'roll';
		rollRemaining = rollDuration;
	} else if (rollOrRest == 'roll') {
		endBell.play();
		rollOrRest = 'rest';
		rollRemaining = rest;
	}

	if (typeof (rollTimer) != 'undefined') {
		rollTimer.terminate();
	}
	rollTimer = undefined;
	rolling = true;

	startRoll();
}

function stopRoll() {
	document.getElementById('updateRollBtn').disabled = false;
	var restElements = document.restOptions.restTime;
	for (var i = 0, iLen = restElements.length; i < iLen; i++) {
		restElements[i].disabled = false;
	}
	var durationElements = document.rollDurationOptions.rollTime;
	for (var i = 0, iLen = durationElements.length; i < iLen; i++) {
		durationElements[i].disabled = false;
	}

	rollFG = 'red';
	updateRollTimer();
	statusMessage = 'PAUSE!';
	updateRollStatus(statusMessage);

	if (typeof (rollTimer) != 'undefined') {
		rollTimer.terminate();
	}
	rollTimer = undefined;
}



function startRollClock() {
	if (typeof(Worker) !== 'undefined') {
		if (typeof(rollClock) == 'undefined') {
			rollClock = new Worker('clockThread.js');
		}
		rollClock.onmessage = function(event) {
			updateRollClock(event.data);
		};
	} else {
		document.getElementById('rollClockDisplay').innerHTML =
		'Sorry, your browser does not support Web Workers...';
	}
}

function updateRollClock(time) {
	var c = document.getElementById('rollClockCanvas');
	var x = c.width / 2;
	var y = c.height / 2;
	//c.style.background = timerBG;
	var ctx = c.getContext('2d');
	ctx.setTransform(1, 0, 0, 1, 0, 0);
	ctx.clearRect(0, 0, c.width, c.height);
	ctx.font = '50px Impact';
	ctx.fillStyle = '#CDCFF8';
	//clockCtx.scale(1.1, 1.2);
	ctx.textAlign = 'center';
	ctx.fillText(time, x, y + 20);
}

function updateRollSettings() {
	rollCount = 0;
	rolling = false;
	rollOrRest = 'roll';

	if (guiTheme == 'light') {
		rollFG = 'black';
	} else if (guiTheme == 'dark') {
		rollFG = 'white';
	}
	rollDuration = document.querySelector(
		'input[name="rollTime"]:checked').value;
	rest = document.querySelector(
		'input[name="restTime"]:checked').value;
	rollRemaining = rollDuration;
	//rollFG = timerFG;
	updateRollStatus('READY!');
	updateRollTimer();


	if (rest == '0') {
		rollConfig = (rollDuration / 60) + 'm single';
	} else {
		rollConfig = (rollDuration / 60) + 'm / ' + rest + 's';
	}
	updateRollCount();
}

function getWinner() {
	if (p1Score > p2Score) {
		p1MatchEnd('pts');
	} else if (p2Score > p1Score) {
		p2MatchEnd('pts');
	} else if (p1Advantage > p2Advantage) {
		p1MatchEnd('adv');
	} else if (p2Advantage > p1Advantage) {
		p2MatchEnd('adv');
	} else if (p1Penalty < p2Penalty) {
		p2MatchEnd('pen');
	} else if (p2Penalty < p1Penalty) {
		p1MatchEnd('pen');
	} else {
		updateStatus('REFEREE\'S CALL!');
		dingBell.play();
	}
}

