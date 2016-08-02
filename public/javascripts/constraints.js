/**
 *
 * @param ms
 */
function convertDuration(ms) {
    var minutes = (ms / (1000*60)) % 60;
    var seconds = (ms / 1000) % 60;
    return minutes + ":" + seconds
}

/**
 * build a Constraint String and add append a <p> element to the jumbotron
 */
function parseConstraints(name, input) {
    var para;
    if(name == 'unary') {
        para = parseUnaryConstraints(input)
    } else {
        para = parseGlobalConstraints(name, input);
    }
    // number of tracks => setNumberOfTracks(n) TODO
    setNumberOfTracks(20, para);
}

/**
 * create a new <p> element parsing values to form its attributes
 *
 * @param input
 * @returns {Element}
 */
function parseUnaryConstraints(input) {
    var selection = document.getElementById("attr-select");
    var attributeName = selection.options[selection.selectedIndex].value;

    var from = $('#slider-from').val();
    var to = $('#slider-to').val();
    console.log(from + " to " + to);
    // if from == to Unary else Range(from, to)

    var para = document.createElement('p');
    para.setAttribute("type", "indexed");
    para.setAttribute("from-index", (from - 1) + "");
    para.setAttribute("to-index", (to - 1) + "");
    var constraintName = getRadioVal('unary-include');
    para.setAttribute("constraint-name", constraintName);
    para.setAttribute("attribute-name", attributeName);
    para.setAttribute("attribute-value", input);

    var trackN;
    if(from == to) trackN = '#' + from;
    else trackN = '#' + from + " to #" + to;
    var text = trackN + " having " + attributeName + " " + getCName(constraintName) + input;

    var node = document.createTextNode(text);
    para.appendChild(node);
    return para;

}

// NO NEED TO SWITCH, YOU CAN USE THE TEXT PARA IN THIS CASE
function getCName(text) {
    switch(text) {
        case "IncludeLarger":
            return "no less than ";
            break;
        case "IncludeSmaller":
            return "no more than ";
            break;
        case "IncludeEquals":
            return "around ";
            break;
    }
}

/**
 *
 * @param name the name of a Constraint
 * @param input the input value of a Constraint
 */
function parseGlobalConstraints(name, input) {
    // make the first char uppercase to parse it as Class instance
    var attrName = name.charAt(0).toUpperCase() + name.substring(1, name.length);
    // get the value of radio input "[name]-ltgt" ('UnarySmaller', 'UnaryLarger')
    var ltgt = getRadioVal(name + "-ltgt");
    var symbol = getRadioToText(ltgt);
    // get the value of radio input "radio-[name]" ('Any', 'All', 'None')
    var anyAllNone = getRadioVal("radio-" + name);
    var text = getRadioToText(anyAllNone);
    var para = document.createElement("p");
    para.setAttribute("type", "simple");
    para.setAttribute("constraint-name", ltgt + anyAllNone);
    para.setAttribute("attribute-name", attrName);
    para.setAttribute("attribute-value", input);
    text += "having " + attrName + symbol + input;
    var node = document.createTextNode(text);
    para.appendChild(node);
    return para;
}

/**
 * when the first new Constraint is added, the first <p> in jumbotron
 * is changed to be syntactically correct
 *
 * @param numberOfTracks the number of tracks of the new playlist
 * @param newConstraint the <p> element holding the new Constraint String
 */
function setNumberOfTracks(numberOfTracks, newConstraint) {
    var div = document.getElementById('input-constraints');
    // first constraint to append:
    // change 'a [n] playlist with no constraints'
    // into 'a [n] playlist with the following constraints:'
    if (isDataClean(div)) {
        document.getElementById("constraints-firstLine-2").innerHTML = "playlist with the following constraints:";
    }
    div.appendChild(newConstraint);
}

/**
 * @returns true if no Constraint is present in 'div'
 * (having 'data-clean' attribute set to true) and switch it to false
 */
function isDataClean(div) {
    switch (div.getAttribute('data-clean').toLowerCase()) {
        case "true":
            div.setAttribute('data-clean', 'false');
            return true;
        default:
            return false;
    }
}
/**
 * IncludeAll if radio box 'All' is checked,
 * IncludeAny if radio box 'Any' is checked,
 * ExcludeAll if radio box 'None' is checked
 *
 * undefined if nothing is checked
 */
function getRadioVal(name) {
    var result;
    var radios = document.getElementsByName(name);
    for (var i = 0; i < radios.length; i++) {
        if (radios[i].checked) {
            result = radios[i].value;
            break;
        }
    }
    return result;
}

function getRadioToText(name) {
    switch (name) {
        // 'AnyAllNone' radio
        case "Any":
            return "any song ";
            break;
        case  "All":
            return "all songs ";
            break;
        case "None":
            return "no songs ";
            break;
        // 'ltgt' radio
        case "UnaryLarger":
            return " > ";
            break;
        case "UnarySmaller":
            return " < ";
            break;
        case "UnaryEquals":
            return " == ";
            break;
    }
}

/**
 *
 */
function resetConstraints() {
    // create fresh firstLine
    document.getElementById('constraints-firstLine-2').innerHTML = "playlist with no constraints";
    // clean 'input-constraints' div and set 'data-clean' to true
    var constraints = document.getElementById('input-constraints');
    constraints.innerHTML = "";
    constraints.setAttribute('data-clean', 'true');
}