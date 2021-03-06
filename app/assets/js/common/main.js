const jsRoutesControllers = jsRoutes.io.bartholomews.musicgene.controllers

function redirect(href) {
    location.href = href;
}

function csrfTokenHeader() {
    return {"Csrf-Token": document.body.getAttribute('data-token')};
}

function flushArray(array) {
    array.splice(0, array.length)
}

function getParentTable(element) {
    return getParentWithTagName(element, 'TABLE');
}

function getParentWithTagName(element, tagName) {
    const curr = element.parentElement;
    return (curr.tagName === tagName) ? curr : getParentWithTagName(curr, tagName);
}

// return true if element has `className` AFTER the toggle
function toggleClass(element, className) {
    if (element.classList.contains(className)) {
        element.classList.remove(className);
        return false;
    } else {
        element.classList.add(className);
        return true;
    }
}