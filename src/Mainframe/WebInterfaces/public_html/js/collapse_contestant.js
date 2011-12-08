$(document).ready(function() {
        setCCollapseHandlers();
        setACollapseHandlers();
});

var openContestants = new Array();
var openAnnouncements = new Array();

function toggledata(target) {
	target.children("div").slideToggle("fast");
	target.toggleClass("open");
}

function setACollapseHandlers(){
    $("ul.collapsible").click(function(e) {
            var $target = $(e.target);
            if( $target.is("label") ) {
                    toggledata($target);
                    var id = $target.attr('for').split("_")[1];
                    if(openAnnouncements.indexOf(id) != -1){
                        openAnnouncements.splice(openAnnouncements.indexOf(id), 1);
                    }else{
                        openAnnouncements.push(id);
                    }
            }
            /*else {
                    $target = $target.parent();
                    if( $target.is("label") ) {
                            toggledata($target);
                    }
            }*/
    }).find("div").hide();
}

function setCCollapseHandlers(){
    $("ol.scorelist").click(function(e) {
            var $target = $(e.target);
            var id;
            if( $target.is(".scorelist div:first-child span") ) {
                    $slide = $target.parent().siblings('.cextrainfo');
                    $slide.slideToggle('fast');
                    id = $target.attr('id');
                    if(openContestants.indexOf(id) != -1){
                        openContestants.splice(openContestants.indexOf(id), 1);
                    }else{
                        openContestants.push(id);
                    }
            }

            //$target.parents('label').find('.cextrainfo').slideToggle("fast");

    }).find(".cextrainfo").hide();
}