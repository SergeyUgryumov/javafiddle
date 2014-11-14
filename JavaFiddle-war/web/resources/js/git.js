/**
 * Send the specified file to the server.
 * @param {type} id
 * @returns {undefined}
 */
function gitSaveFile(id) {
    if(arguments.length === 0) {
        id = getCurrentFileID();
        addCurrentFileText();
        $('#latest_update').text("Saving...");
    }
    
    var time = new Date().getTime();
    $.ajax({
        url: PATH + '/webapi/git/file',
        type:'POST', 
        data: {id: id, timeStamp: time, value: getOpenedFileText(id)},
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
/**
 * Saves all changed files. 
 * @returns {undefined}
 */
function gitSaveAllFiles() {
    addCurrentFileText();
    $('#latest_update').text("Saving...");
    modifiedList().forEach(function(entry) {
        gitSaveFile(entry);
    });
    $('#latest_update').text("All files saved");
}
/**
 * Sends a new object to the server
 */
function gitAddObject() {
    
}
// rename objects and do 'git-delete and git-add' commands
function gitRenameObject() {
    
}
// delete an existing object
function gitDeleteObject() {
    
}
// get the returned string from git-status
function gitGetStatus() {
    
}
// send the commit command
function gitCommit(message) {
    
}