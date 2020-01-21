
function submitIfOnline() {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open("HEAD", "index.http", false); // false means synchronous
    try {
        xmlHttp.send();
        if (xmlHttp.status == 200) {
            return true;
        }
    } catch (e) {
        // nothiing to do;
    }

    alert("Keine Verbindung vorhanden. Bitte versuchen Sie es später nochmals!");
    return false;
}
