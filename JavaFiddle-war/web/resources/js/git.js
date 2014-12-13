/**
 * Make the commit with the specified message.
 * @param {String} message - commit message
 * @returns {undefined}
 */
function gitCommit(message) {
    saveProject();
    $.ajax({
        url: PATH + "/webapi/git/commit",
        type: 'POST',
        async: false,
        data: {'commitMessage': message},
        success: function(data) { //Supposed to return the hash of the commited project.
            console.log('Successfully passed through /webapi/git/commit');
            $('#latest_update').text('Project commited with hash ' + data);
        },
        error: function(data) {
            $('#latest_update').text('Failed to commit');
        }
    })
}

/**
 * Sends a new object to the server
 * @param id {String} id of the file we will add now
 */
function gitAddFile(id) {
    saveFile(id);
    
    $.ajax({
        URL: PATH + 'webapi/git/add',
        type: 'POST',
        data: {'classId':id},
        success: function(data) {
        },
        error: {
        }
    })
}
// get the returned string from git-status
function gitGetStatus() {
    
}

function addDefaultFileToGit() {
    var id
    $.ajax({
        url: PATH + '/webapi/git/addDefaultClass',
        type: 'POST',
        async: false,
        success: function(data) {
            console.log("addDefaultFile: " + data);
            id = data;
        },
        error: function() {
            console.log("Failed to add default class to git");
        }
    })
    return id;
}