var vTimer = 2;
var fInterval = 20;
var width = 510;
var timerMove = null;
var showCant = 0;
var totalCant = new Array(0, 0);
var selected = new Array(0, 0);
var pos = 0;
var idTable = "tableMove";
var anterior = null;
var prefix = "";
this.e$ = function(a) {
	return document.getElementById(a)
};
function init() {
	document.focus;
	document.onkeydown = keySelector;
	if (pos != 0) {
		var a = e$(idTable);
		var c = e$("molule_form:group_" + pos + mod);
		c.style.display = "";
		e$("modules").style.display = "none";
		desp = -1 * width;
		a.style.left = desp + "px";
		prefix = mod;
		next = c;
		traceUp(true, sel)
	}
	var b = getSelected();
	if (b == null) {
		b = getSelected(true)
	}
	if (b != null) {
		checkSelected()
	}
}
var mod;
var sel;
function initVars(b, g, a, d, f, e, c) {
	totalCant[0] = b;
	totalCant[1] = g;
	showCant = a;
	mod = f;
	pos = 0;
	if (e != null) {
		pos = 1;
		selected[0] = f;
		numSelected = f;
		sel = c;
		totalCant[pos] = g
	}
	if (d < b) {
		selected[pos] = d
	}
}
function keySelector(c) {
	var a;
	if (document.all) {
		a = window.event.keyCode
	} else {
		a = c.which
	}
	switch (a) {
	case 38:
		if (selected[pos] - showCant >= 0) {
			setAnterior(getSelected(true));
			selected[pos] -= showCant;
			checkSelected(true)
		}
		break;
	case 40:
		setAnterior(getSelected());
		if (selected[pos] + showCant < totalCant[pos]) {
			selected[pos] += showCant
		} else {
			selected[pos] = totalCant[pos] - 1
		}
		checkSelected();
		break;
	case 37:
		if (selected[pos] - 1 >= 0) {
			setAnterior(getSelected(true));
			selected[pos] -= 1;
			checkSelected(true)
		}
		break;
	case 39:
		if (selected[pos] + 1 < totalCant[pos]) {
			setAnterior(getSelected());
			selected[pos] += 1;
			checkSelected()
		}
		break;
	case 13:
		var b = getSelected();
		b.onclick();
		break;
	case 27:
	case 8:
	case 1:
	case 0:
		if (pos == 0) {
			break
		}
		var b = getSelected();
		uncheckOne(b);
		move(false);
		break;
	default:
		break
	}
	return false
}
var next;
var numSelected;
function move(d, a, f, c, b) {
	var e = e$("modules");
	if (!d) {
		if (pos == 0) {
			return
		}
		prefix = "";
		pos--;
		e.style.display = "";
		next.style.display = "none";
		selected[pos] = numSelected;
		anterior = getSelected();
		traceUp(false)
	} else {
		if (pos == 1) {
			e.onclick();
			return
		}
		traceUp(true, b);
		anterior = getSelected();
		prefix = c;
		numSelected = c;
		selected[pos] = numSelected;
		uncheckOne(anterior);
		pos++;
		totalCant[pos] = f;
		next = e$(a);
		next.style.display = "";
		e.style.display = "none"
	}
	checkSelected();
	timerMove = setInterval(executeMovement, vTimer)
}
function executeMovement() {
	var b = e$(idTable);
	var a = getPos(b.style.left);
	var c;
	if (pos == 0) {
		c = a + fInterval;
		if (c > 0) {
			c = 0;
			b.style.left = c + "px";
			clearInterval(timerMove);
			return
		} else {
			b.style.left = c + "px"
		}
	} else {
		c = a - fInterval;
		if (c < -1 * width) {
			c = -1 * width;
			b.style.left = c + "px";
			clearInterval(timerMove);
			return
		} else {
			b.style.left = c + "px"
		}
	}
}
function getPos(d) {
	var c = new String(d);
	var b = c.replace("px", "");
	var a = new Number(b);
	return a
}
function getSelected(d) {
	var a = 0;
	if (d == null) {
		while (selected[pos] < totalCant[pos]) {
			var b = "action_" + pos + selected[pos] + prefix;
			var c = e$(b);
			if (c.className.match("lockNode")) {
				selected[pos]++;
				a++
			} else {
				return c
			}
		}
		if (a == totalCant[pos]) {
			return null
		}
		selected[pos] = totalCant[pos] - 1;
		return getSelected(true)
	} else {
		while (selected[pos] >= 0) {
			var b = "action_" + pos + selected[pos] + prefix;
			var c = e$(b);
			if (c.className.match("lockNode")) {
				selected[pos]--;
				a--
			} else {
				return c
			}
		}
		if (a == totalCant[pos]) {
			return null
		}
		selected[pos] = 0;
		return getSelected()
	}
}
function setSelected(c) {
	selected[pos] = c;
	var a = "action_" + pos + selected[pos] + prefix;
	var b = e$(a);
	anterior = b
}
function checkSelected(b) {
	var a = getSelected(b);
	if (anterior != null) {
		anterior.setAttribute("class", "unchecked");
		anterior.className = "unchecked"
	} else {
		anterior = a
	}
	a.setAttribute("class", "checked");
	a.className = "checked"
}
function setAnterior(a) {
	if (anterior == null) {
		anterior = a
	}
	if (a.id != anterior.id) {
		anterior = a
	}
}
function uncheckOne(a) {
	a.setAttribute("classname", "unchecked");
	a.className = "unchecked";
	selected[pos] = 0
}
function traceUp(b, a) {
	if (b) {
		e$("subtraceValue").innerHTML = a;
		e$("subtrace").style.display = "";
		e$("trace2").style.display = "";
		e$("trace1").style.display = "none"
	} else {
		e$("subtrace").style.display = "none";
		e$("trace2").style.display = "none";
		e$("trace1").style.display = ""
	}
}
function clickext(url){
	window.location = url;
}
function gotourl(b, c, a, d) {
	window.location = b + "/modCommons/modHome/home" + c.replace("*", "")
			+ "?moduleName=" + a + "&idancestors=" + d + "a&idFuncionalidad="
			+ d
}
function click2(a, b, d, c, e) {
	window.location = a + "/modCommons/modSelector/" + d
			+ "/themes/modSelector" + b.replace("*", "") + "?idFuncionalidad="
			+ c + "&idEntidad=" + e
}
document.onkeydown = keySelector;