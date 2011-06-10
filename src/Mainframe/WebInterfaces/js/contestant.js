$(document).ready(function() {
        setTimeout("updateLeaderboard()",30000);
        var flgsubmsgt;
        //$('#chart1').append('<div id="toolTip" style="position:absolute;display:none;background:#E5DACA;padding:4px;"></div>');

        $("#flagsubmit").click(function() {
            if(flgsubmsgt != undefined)
	    	clearTimeout(flgsubmsgt);
            $("#flagsubmit").attr("disabled", true);
        // validate and process form here
            var flag = $("input#flaginputfield").val();
            var dataString = 'ajax=flagsub&sub_flag=Claim%20Flag&flag=' + flag;
            
            $.ajax({  
                type: "POST",  
                url: "index.php",  
                data: dataString,  
                success: function(data) {
                    $('#flagresponse').remove();
                    //$('#prepareblock').append(data);
                    //var len = $('#flagresponse').children().length;
                    //$('#flagdisplay').animate({bottom: (len*2+2)+'em'});
                    $('#flagform').prepend(data);
                    $('#flagdisplay').animate({bottom: (40+$('#flagresponse').outerHeight())+'px'});
                    $('#flagresponse').slideDown();
                    flgsubmsgt = setTimeout(function(){
                        $('#flagresponse').fadeOut(1000,function(){$('#flagresponse').remove()});
                        $('#flagdisplay').delay(1000).animate({bottom: '2em'});
                    },10000);
                },
                error: function() {
                    $('#flagresponse').remove();
                    $('#flagform').prepend("<div id=\"flagresponse\" style=\"display: none; right: 0pt; left: 0pt; bottom: 2em; border: 1px solid red; padding: 3px; background: none repeat scroll 0pt 0pt rgb(255, 170, 170);\"> Error while connecting to the mainframe!</div>");
                    
                    $('#flagdisplay').animate({bottom: (40+$('#flagresponse').outerHeight())+'px'});
                    $('#flagresponse').slideDown();
                    
                    flgsubmsgt = setTimeout(function(){
                        $('#flagresponse').fadeOut(1000,function(){$('#flagresponse').remove()});
                        $('#flagdisplay').delay(1000).animate({bottom: '2em'});
                    },10000);
                },
                complete:  function(){
                    $("#flagsubmit").attr("disabled", false);
                }
            });  
            
            return false;  
          });
});

function updateLeaderboard(){
    var dataString = 'ajax=leaderboard';

    $.ajax({
        type: "POST",
        url: "index.php",
        data: dataString,
        success: function(data) {
            $('ol.scorelist').remove();
            $('#scorecontainer').append(data);
            flgsubmsgt = setTimeout("updateLeaderboard()",30000);
        }
    });
}
