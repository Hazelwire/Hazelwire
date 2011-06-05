$(document).ready(function() {
	
	function toggledata(target) {
		target.children().slideToggle("fast");
		target.toggleClass("open");
	}
	
	$("ul.collapsible").click(function(e) {
		var $target = $(e.target);
		if( $target.parent().parent().is("div") ) {
			toggledata($target);
		}
		else {
			$target = $target.parent();
			if( $target.parent().parent().is("div") ) {
				toggledata($target);
			}
		}
	}).find("div").hide();
        
        
        
        
        $("#flagsubmit").click(function() {
            $("#flagsubmit").attr("disabled", true);
        // validate and process form here
            var flag = $("input#flaginputfield").val();
            var dataString = 'ajax=flagsub&sub_flag=Claim%20Flag&flag=' + flag;
            
            $.ajax({  
                type: "POST",  
                url: "index.php",  
                data: dataString,  
                success: function(data) {
                    var len = $('#flagresponse').children().length;
                    $('#flagdisplay').animate({bottom: (len*2+2)+'em'});
                    $('#flagform').before(data);
                    $('#flagresponse').slideDown();
                    setTimeout(function(){
                        $('#flagresponse').fadeOut(1000,function(){$('#flagresponse').remove()});
                        $('#flagdisplay').delay(1000).animate({bottom: '2em'});
                    },15000);
                },
                error: function() {
                    $('#flagdisplay').animate({bottom: '4em'});
                    $('#flagform').before("<div id=\"flagresponse\" style=\"display: none; right: 0pt; left: 0pt; bottom: 2em; border: 1px solid red; padding: 3px; background: none repeat scroll 0pt 0pt rgb(255, 170, 170);\"> Error while connecting to the mainframe! </div>");
                    $('#flagresponse').slideDown();
                    setTimeout(function(){
                        $('#flagresponse').fadeOut(1000,function(){$('#flagresponse').remove()});
                        $('#flagdisplay').delay(1000).animate({bottom: '2em'});
                    },15000);
                },
                complete:  function(){
                    $("#flagsubmit").attr("disabled", false);
                }
            });  
            
            return false;  
          });
});


