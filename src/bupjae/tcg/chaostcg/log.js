function createChatMessage(m) {
    return $("<p>")
            .append($("<span class='nickname'>").text(m.getNickname()))
            .append($("<span> :: </span>"))
            .append($("<span>").text(m.getText()));
}

function createGameMessage(m) {
    var ret = $("<p>");
    var n = m.getSpanCount();
    for (var i = 0; i < n; i++) {
        var span = m.getSpan(i);
        var kind = span.getSpanKindCase();
        if (kind.equals(kind.TEXT)) {
            ret.append($("<span>").text(span.getText()));
        } else if (kind.equals(kind.NICKNAME)) {
            ret.append($("<span class='nickname'>").text(span.getNickname()));
        } else if (kind.equals(kind.GAME_OBJECT)) {
            // TODO
        }
    }
    return ret;
}

function addLogMessage(o) {
    var kind = o.getMessageKindCase();
    if (kind.equals(kind.CHAT)) {
        $("body").append(createChatMessage(o.getChat()));
    } else if (kind.equals(kind.GAME)) {
        $("body").append(createGameMessage(o.getGame()));
    }
}

function addLogMessages(l) {
    var n = l.getLogCount();
    for (var i = 0; i < n; i++) {
        addLogMessage(l.getLog(i));
    }
}
