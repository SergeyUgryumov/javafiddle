var PATH = "";
var javaEditor;

// LOADING PAGE

$(document).ready(function(){
   setContentHeight();
   loadMainMenu();
   loadLiHarmonica();
   buildTree();
   loadTreeOperation();
   loadTabs();
   loadToogles();
   moment.lang('ru');
   $("body").click(function() {
       closeAllPopUps();
   });
});

$(window).resize(function() {
    setContentHeight();
});

function setContentHeight() {
    var MINSCREENWIDTH = 600;
    var $header = $('#header');
    var $tree = $("#treepanel");
    var $code = $("#codetext");
    var $tabpanel = $("#tabpanel");
    var height = $(window).height() - $header.height() - 32;
    
    $tree.height(height);
    $code.height(height);
    
    javaEditor.setSize($code.width(), $code.height());
    
    /**
     * set position of tabpanel
     */
    var sizer = parseInt($(".CodeMirror-sizer").css("margin-left"));
    var margin = ($(window).width()) < MINSCREENWIDTH ? 340 : (33 + $tree.width() + sizer);
    $tabpanel.css("margin-left", (margin - 340) + "px");
    $tabpanel.width($(window).width() - margin - 50);
}


// MENU

function loadMainMenu() {
    $menu = $("#main_menu");
    $('#main_menu div.link').each(function() {
        $parent = $(this).parent();
        if($parent.children().length > 1) {
            $(this).click(function() {  
                $li = $(this).parent();
                if($li.hasClass('active')) {
                    $li.find('ul').removeClass('show');
                    $li.removeClass('active');
                } else {
                    $menu.find('.active').removeClass("active");
                    $menu.find('.show').removeClass('show');
                    $li.addClass('active');
                    $li.find('ul').addClass('show');
                }
            });
        }
    });
    
    $menu.click(function(event){
        $("#context_menu").remove();
        event.stopPropagation();
    });
}


// TABS

function loadTabs() {
    var opened = getCurrentFileID();
    var data = openedTabs();
    if (data === null)
        return;
    for(var i = 0; i < data.length; i++) {
        var file = getFileDataById(data[i]);
        var cl = file["type"];
        var name = file["name"];
        var li = addTabToPanel(data[i], name, cl);
        if (data[i] == opened)
            selectTab(li);
    }
}

function openTabFromTree($el) {
    var id = $el.closest('li').attr('id') + "_tab";
    var name = $el.text();
    var cl = $el.attr('class');
    
    var li;
    if(!isOpened(id))
        li = addTabToPanel(id, name, cl);
    else {
        li = document.getElementById(id);
        li = $("#tabpanel").find(li);
    }
    selectTab(li);
}

function addTabToPanel(id, name, cl) {
    pushOpenedTab(id);
    
    var li = $('<li id="'+ id +'" class="'+ cl +'" onclick="selectTab($(this))">'+ name +'<div class="close" onclick="closeTab($(this).parent())"></div></li>');
    $("#tabpanel").append(li);
    return li;
}

function selectTab(li) {
    var id = li.attr("id");
    
    if (openedTabs().indexOf(id) == -1)
        return false;
    
    $tabs = $("#tabpanel");
    
    if (id !== getCurrentFileID()) {
        addCurrentFileText(getCurrentFileID());
        setCurrentFileID(id);
    }
    
    javaEditor.clearHistory();
    $tabs.find(".active").removeClass("active");
    li.addClass("active");
    getCurrentFileText(id);
}

function closeTab(parent) {
    var id = parent.attr("id");
    clearOpenedTab(id);
    removeCurrentFileText(id);
    parent.remove();
    setCurrentFileID("");
    
    id = getLastOpenedTab();
    var li = document.getElementById(id);
    li = $("#tabpanel").find(li);
    selectTab(li);
}


// TREE

function buildTree() {
    $("#tree").empty();
    $.ajax({
        url: PATH + '/webapi/tree',
        type: 'GET',
        dataType: "json",
        async: false,
        success: function(data) {
            for (var i = 0; i < data.projects.length; i++) {
                var proj = data.projects[i];
                $('#tree').append('<li id = "node_' + proj.id + '" class="open"><a href="#" class="root">' + proj.name + '</a><ul id ="node_' + proj.id + '_src"\></li>');
                $('#node_' + proj.id + '_src').append('<li id = "node_' + proj.id + '_srcfolder" class="open"><a href="#" class="sources">src</a><ul id ="node_' + proj.id + '_list"\></li>');
                document.getElementById("projectname").innerHTML = proj.name;
                for (var j = 0; j < proj.packages.length; j++) {
                    var pck = proj.packages[j];
                    $('#node_' + pck.parentId + '_list').append('<li id = "node_' + pck.id + '"><a href="#" class="package">' + pck.name + '</a><ul id ="node_' + pck.id + '_list"\></li>');
                }   
                for (var j = 0; j < proj.packages.length; j++) {
                    var pack = proj.packages[j];
                    for (var k = 0; k < pack.files.length; k++) {
                        var file = pack.files[k];
                        $('#node_' + pack.id + '_list').append('<li id = "node_' + file.id + '"><a href="#" class="' + file.type + '">' + file.name + '</a></li>');
                     }
                }   
            }
            $(function () {
                $('#tree').liHarmonica({
                    onlyOne: false,
                    speed: 100
                });
            });
        }
    });
}

function loadTreeOperation() {
    $("#tree a").each(function() {
        this.oncontextmenu = function() {return false;};  
        $(this).mousedown(function(event) {
            var classname = $(this).attr("class").split(" ")[0];
            switch(classname) {
                case "class": 
                case "interface": 
                case "exception": 
                case"annotation": 
                case "runnable":
                    switch(event.which) {
                        case 1:
                            openTabFromTree($(this));
                            break;
                        case 3: 
                            showContextMenu($(this), event, "file");
                            break;
                    }
                    break;
                case "package":
                    switch(event.which) {
                        case 3: 
                            showContextMenu($(this), event, "package");
                            break;
                    }
                    break;
                case "root":
                    switch(event.which) {
                        case 3: 
                            showContextMenu($(this), event, "root");
                            break;
                    }
                    break;
            }
        });
    });
}

function loadLiHarmonica() {
    (function ($) {
        $.fn.liHarmonica = function (params) {
            var p = $.extend({
                currentClass: 'cur',
                onlyOne: false,
                speed: 100
            }, params);
            return this.each(function () {

                var el = $(this).addClass('harmonica'), linkItem = $('ul', el).prev('a');

                el.children(':last').addClass('last');
                $('ul', el).each(function () {
                    $(this).children(':last').addClass('last');
                    el.find('.open').children('ul').addClass('opened');
                });

                $('ul', el).prev('a').addClass('harFull');


                el.find('.' + p.currentClass).parents('ul').show().prev('a').addClass(p.currentClass).addClass('harOpen');

                linkItem.on('click', function () {
                    if ($(this).next('ul').is(':hidden')) {
                        $(this).addClass('harOpen');
                    } else {
                        $(this).removeClass('harOpen');
                    }
                    if (p.onlyOne) {
                        $(this).closest('ul').closest('ul').find('ul').not($(this).next('ul')).slideUp(p.speed).prev('a').removeClass('harOpen');
                        $(this).next('ul').slideToggle(p.speed);
                    } else {
                        $(this).next('ul').stop(true).slideToggle(p.speed);
                    }
                    return false;
                });
            });
        };
    })(jQuery);
}

function addPackage(id) {
    $input = $("#addpackage input");
    var name = $input.val().toLowerCase();
    var dotname;
    
    if(!name.endsWith(".")) {
        dotname = name + ".";
    }
    
    if(checkAddPackageInputName(dotname, id)) {
        $.ajax({
            url: PATH + '/webapi/tree/addPackage',
            type: 'POST',
            data: {project_id: id, name: name},
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                $('#popup_bug').togglePopup(); 
                buildTree();
            }
        });  
    }
 
}

function addFile(id) {
    $input = $("#addfile input#class");
    var name = $input.val();
    var type = $("#addfile #filetype").attr("class");

    if(name.endsWith(".java")) {
        name = name.substring(0, name.length - 5);
    }
    
    if(isRightClassName(name)) {
        $.ajax({
            url: PATH + '/webapi/tree/addFile',
            type: 'POST',
            data: {package_id: id, name: name, type: type},
            contentType: "application/x-www-form-urlencoded",
            success: function() {
                $('#popup_bug').togglePopup(); 
                buildTree();
            }
        });   
    } else {
        $("#addfile #result-msg").text("Неверное название класса. \n\
                            Допустимы лишь латинские символы и цифры. \n\
                            Название класса не должно начинаться с цифры.");
        $("#addfile #result-msg").css("color", "red");
        $("#addfile #result-msg").css("font-size", "13px");
    }
}

function removeFromProject(id) {
    var idString = String(id);
    $.ajax({
        url: PATH + '/webapi/tree/remove',
        type: 'POST',
        data: idString,
        contentType: "application/json",
        success: function() {
            var removeId = "#" + idString;
            $(removeId).remove();
            $("#context_menu").remove();
            $('#popup_bug').togglePopup(); 
        }
    });     
}


// TOGGLES

function loadToogles() {
    $.fn.alignCenter = function() {
       var marginLeft =  - $(this).width()/2 + 'px';
       var marginTop =  - $(this).height()/2 + 'px';
       return $(this).css({'margin-left':marginLeft, 'margin-top':marginTop});
    };

    $.fn.togglePopup = function(){
        if($('#popup').hasClass('hidden')) {
            $("#popup").empty();
            $('#opaco').height($(document).height()).toggleClass('hidden').fadeTo('slow', 0.7)
                     .click(function(){$(this).togglePopup();});

            $('#popup')
              .html($(this).html())
              .alignCenter()
              .toggleClass('hidden');
        } else  {
            $('#opaco').toggleClass('hidden').removeAttr('style').unbind('click');
            $('#popup').toggleClass('hidden');
        }
    };
}

function showPopup(content, params) {
    var options = {
        width: 300,
        height: 300
    };
    if(arguments.length > 1) {
        options = params;
    }
    var html = "<div id=\"opaco\" class=\"hidden\"></div>";
    html += "<div id=\"popup\" class=\"hidden\"></div>";
    html += "<div id=\"popup_bug\" class=\"hidden\">";
    html += "<div class=\"bug\">";
    if( Object.prototype.toString.call(content) === '[object String]' ) {
        html += (content === "" || content === null) ? "" : content;
    }
    html += "</div>";
    html += "</div>";
    
    $div = $('<div/>', {
        html: html
    });
    
    $("body").append($div);
    $("#popup").attr("style", "height: " + options.height + "px !important; width: " + options.width + "px !important");
    if( Object.prototype.toString.call(content) === '[object Object]' ) {
        $("#popup_bug .bug").html("");
        $("#popup_bug .bug").append(content);
    }
    $('#popup_bug').togglePopup();
}

function showContextMenu($el, event, classname) {
    $("#context_menu").remove();
    var elementname = $el.html();
    var id = $el.parent().attr("id");
    var x = event.clientX;
    var y = event.clientY;
    var context_menu_id = "context_menu";
    $ul = $('<ul/>', {
        id: context_menu_id
    });
    $ul.css('margin-left', x + 'px');
    $ul.css('margin-top', y + 'px');
    $ul.appendTo('body');
   
    switch(classname) {
        case "file":
            $li = $('<li/>', {text: 'Открыть'});
            $li.click(function() {
                openTabFromTree($el);
                $ul.remove();
            });
            $li.appendTo($ul);
            
            $('<li/>', {text: 'Переименовать'}).appendTo($ul);
            
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
        case "package":
            $li = $('<li/>', {text: 'Добавить...'});
            $li.click(function() {
                showAddFileWindow(id);
                $ul.remove();
            });
            $li.appendTo($ul);
            $('<li/>', {text: 'Переименовать'}).appendTo($ul);
            
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
        case "root":
            $('<li/>', {text: 'Запустить'}).appendTo($ul);
            $('<li/>', {text: 'Переименовать'}).appendTo($ul);
            $li = $('<li/>', {text: 'Добавить пакет'});
            $li.click(function() {
                showAddPackageWindow(id); 
                $ul.remove();
            });
            $li.appendTo($ul);
            $('<li/>', {text: 'Настройки проекта'}).appendTo($ul);
            $li = $('<li/>', {text: 'Удалить'});
            $li.click(function() {
                deleteFromProject(id, classname, elementname);
                $ul.remove();
            });
            $li.appendTo($ul);
            break;
    }
    $("#context_menu").click(function(event) {
        event.stopPropagation();
    });
}

function closeAllPopUps() {
    $("#context_menu").remove();
    $("#main_menu").find('.active').removeClass("active");
    $("#main_menu").find('.show').removeClass('show'); 
}

function deleteFromProject(id, classname, name) {
    $div = drawDeleteWindow(id, classname, name);
    
    var params = {
        width: 500,
        height: 150
    };
    showPopup($div, params);
}


// WINDOWS

function drawDeleteWindow(id, classname, name) {
    var cn = "";
    switch(classname) {
        case "file":
            cn = "файл";
            break;
        case "package":
            cn = "пакет";
            break;
        case "root":
            cn = "проект";
            break;
    }
    $div = $('<div/>', {
        id: "delete_confirm"
    });
    $div.append("<span class='title'>Вы уверены, что хотите удалить " + cn + " " + name + "?</span>");
    
    $buttons = $('<div/>', {
        id: "confirm-buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Подтвердить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr("onclick", "removeFromProject('"+ id +"')");
    
    $decline = $('<div/>', {
        text: "Отменить",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    return $div;
}

function showAddPackageWindow(project_id) {
    $div = drawAddPackageWindow(project_id);
    var params = {
        width: 500,
        height: 180
    };
    showPopup($div, params);
}

function showAddFileWindow(package_id) {
    $div = drawAddFileWindow(package_id);
    var params = {
        width: 600,
        height: 380
    };
    showPopup($div, params);
}

function drawAddPackageWindow(id) {
    $div = $('<div/>', {
        id: "addpackage" 
    });
    
    $input = $('<input/>', {
        type: "text",
        value: ""
    });
    $(document).on('input', "#addpackage input", function() {
        checkAddPackageInputName($("#addpackage input").val(), id);
        var val = $("#addpackage input").val();
        $("#addpackage input").val(val.toLowerCase());
    });
    
    $div.append("<div class='inputname'>Название:</div>");
    $div.append($input);
    $div.append("<div id='result-msg'></div>");
    
    $buttons = $('<div/>', {
        id: "confirm-buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Добавить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr( "onclick", "addPackage('"+ id +"')");
    
    $decline = $('<div/>', {
        text: "Отменить",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.css("margin-top", "18px");
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    
    return $div;
}

function drawAddFileWindow(id) {
    var projectname = getProjectName(id);
    var packagename = $(document.getElementById(id)).children("a").text();
    var fullpath = "/" + projectname + "/" + packagename.replace(/\./g, '/') + "/";
    
    $div = $('<div/>', {
        id: "addfile" 
    });
    
    $class = $('<input/>', {
        type: "text",
        id: "class",
        value: ""
    });
    $(document).on('input', "#addfile input#class", function() {
        var val = $("#addfile input#path").val();
        $("#addfile input#path").val(val.substring(0, val.lastIndexOf("/") + 1) + $("#addfile input#class").val() + ".java");
    });
    
    $type = $('<div/>', {
        id: "filetype",
        class: "class"
    });
    
    $type.attr("onclick", "showTypesList()");
    
    $typelist = $('<ul/>', {
        id: "typelist",
        class: "hidden"
    });
    $typelist.append("<li class='class' onclick=\"setFileType('class')\">Class</li>");
    $typelist.append("<li class='interface' onclick=\"setFileType('interface')\">Interface</li>");
    $typelist.append("<li class='runnable' onclick=\"setFileType('runnable')\">Runnable class</li>");
    $typelist.append("<li class='annotation' onclick=\"setFileType('annotation')\">Annotation</li>");
    $typelist.append("<li class='exception' onclick=\"setFileType('exception')\">Exception</li>");
    
    $div.append($type);
    $div.append($typelist);
    $div.append("<div class='inputname'>Название:</div>");
    $div.append($class);
    
    $project = $('<input/>', {
        type: "text",
        id: "project",
        class: "addfileinputs",
        value: projectname
    });
    
    $project.prop('disabled', true);
    $div.append("<div class='inputname addfilename'>Проект:</div>");
    $div.append($project);
    
    $package = $('<input/>', {
        type: "text",
        id: "package",
        class: "addfileinputs",
        value: packagename
    });
    
    $package.prop('disabled', true);
    $div.append("<div class='inputname addfilename'>Пакет:</div>");
    $div.append($package);
    
    $fullpath = $('<input/>', {
        type: "text",
        id: "path",
        class: "addfileinputs",
        value: fullpath
    });
    
    $fullpath.prop('disabled', true);
    $div.append("<div class='inputname addfilename'>Путь до файла:</div>");
    $div.append($fullpath);
    
    $div.append("<div id='result-msg'></div>");
    
    $buttons = $('<div/>', {
        id: "confirm-buttons"
    });
    
    $confirm = $('<div/>', {
        text: "Добавить",
        id: "confirm",
        class: "button"
    });
    $confirm.attr( "onclick", "addFile('"+ id +"')");
    
    $decline = $('<div/>', {
        text: "Отменить",
        id: "decline",
        class: "button"
    });
    $decline.attr("onclick", "$('#popup_bug').togglePopup();");
    
    $buttons.css("margin-top", "18px");
    $buttons.append($confirm);
    $buttons.append($decline);
    
    $div.append($buttons);
    
    return $div;
}

function setFileType(classname) {
    $("#addfile div#filetype").removeClass();
    $("#addfile div#filetype").addClass(classname);
    $("#addfile ul#typelist").addClass("hidden");
}

function showTypesList() {
    if(!$("#addfile ul#typelist").hasClass("hidden")) {
            $("#addfile ul#typelist").addClass("hidden");
    } else {
        $("#addfile ul#typelist").removeClass("hidden");
    }
}

function checkAddPackageInputName(name, id) {
    var result = false;
    if(name.endsWith(".")) {      
        $.ajax({
            url: PATH + '/webapi/tree/package',
            type: 'GET',
            dataType: "json",
            async: false,
            data: {name: String(name.substring(0, name.length - 1)), project_id: String(id)},
            success: function(data) {
                switch(data) {
                    case "wrongname":
                        $("#result-msg").text("Неверное название пакета. \n\
                            Допустимы лишь латинские символы и цифры. \n\
                            Название пакета не должно начинаться с цифры.");
                        $("#result-msg").css("font-size", "13px");
                        break;
                    case "used":
                       $("#result-msg").text("Это имя пакета уже используется в вашем проекте.");
                       $("#result-msg").css("font-size", "15px");
                       break;
                    case "unknown":
                       $("#result-msg").text("Неизвестная ошибка.");  
                       $("#result-msg").css("font-size", "15px");
                       break;
                    case "ok":
                        result = !result;
                        break;
                }
            }
        }); 
        if(!result) {
            $("#result-msg").css("color", "red");
        }
    } else {
        $("#result-msg").text("");
        result = !result;
    } 
    return result;
}

function isRightClassName(name) {
    var result = false;
    
    $.ajax({
        url: PATH + '/webapi/tree/classfile',
        type: 'GET',
        data: {name: name},
        dataType: "json",
        async: false,
        success: function(data) {
            result = data;
        }
    });  
    
    return result;
}


// FILE REVISIONS (SERVICES)

function postFileRevision(id) {
    if(arguments.length == 0) {
        id = getCurrentFileID();
        addCurrentFileText(id);
    }
    var time = moment().format("DD.MM.YYYY HH:mm:ss");
    var dummy = {
        id: id,
        timeStamp: time,
        value: javaEditor.getValue()
    };
    $.ajax({
        url: PATH + '/webapi/revisions',
        type:'POST', 
        data: JSON.stringify(dummy),
        contentType: "application/json",
        success: function() {
            document.getElementById("latest_update").innerHTML = "Все изменения сохранены";
        }
    });
}

function getFileRevision(id) {
    if(arguments.length == 0)
        id = getCurrentFileID();
    $.ajax({
        url: PATH + '/webapi/revisions',
        type:'GET',
        data: {id : id},
        dataType: "json",
        contentType: "application/json",
        success: function(data) {
            javaEditor.setValue(data.value);
            if (data.timeStamp == "")
                document.getElementById("latest_update").innerHTML = "Последнее изменение: еще не сохранялось";    
            else {
                var earlier = moment(data.timeStamp, "DD.MM.YYYY HH:mm:ss");
                document.getElementById("latest_update").innerHTML = "Последнее изменение: " + earlier.from(moment());
            }
        }
    });
}

function savingTabs() {
    var opened = getCurrentFileID();
    var data = JSON.parse(getCookie('openedtabs'));
    if (data === null)
        return;
    for(var i = 0; i < data.length; i++)
        postFileRevision(data[i]);
}


// UTILS

String.prototype.endsWith = function(suffix) {
    return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

function getFileDataById(id) {
    var filedata;

    $.ajax({
        url: PATH + '/webapi/tree/filedata',
        type: 'GET',
        data: {id: id},
        dataType: "json",
        async: false,
        success: function(data) {
            filedata = data;
        }
    }); 
    
    return filedata;
}

function getProjectName(id) {
    var name;
    $.ajax({
        url: PATH + '/webapi/tree/projectname',
        type: 'GET',
        dataType: "json",
        data: {id: id},
        async: false,
        success: function(data) {
            name = data;
        }
    }); 
    return name;
}