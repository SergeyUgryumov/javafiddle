function addFile(id) {
    if(arguments.length === 0) {
        id = getCurrentFileID();
        addCurrentFileText();
        $('#latest_update').text("Saving...");
    }
    
    var time = new Date().getTime();
    $.ajax({
        url: PATH + '/webapi/git/file',
        type:'POST', 
        data: {id: id, value: getOpenedFileText(id)},
        success: function() {
            unModifiedTab(id);
            addCurrentFileTimeStamp(time);
            if (isCurrent(id))
                $('#latest_update').text("All changes saved.");
        },
        error: function(jqXHR) {
            if (jqXHR.status === 406)
                $('#latest_update').text("Saving isn't acceptable.");
        }
    });
}