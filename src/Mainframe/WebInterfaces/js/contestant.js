$(document).ready(function() {
        setTimeout("updateLeaderboard()",30000);
        setTimeout("updateAnnouncements()",30000);
        setTimeout("updatePlotData()",5*60000);
        var flgsubmsgt;
        var endgame = false;
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
                dataType: 'json',
                data: dataString,  
                success: function(data) {
                    $('#flagresponse').remove();
                    if(data!=null && data.action == "flagsub"){
                        $('#flagform').prepend(data.reply);
                        
                    }else if(data!=null && data.action == "endgame"){
                        $('#flagform').prepend("<div id=\"flagresponse\" style=\"display: none; right: 0pt; left: 0pt; bottom: 2em; border: 1px solid red; padding: 3px; background: none repeat scroll 0pt 0pt rgb(255, 170, 170);\"> The game has already ended, you cannot submit flags anymore! </div>");
                        endgame = true;
                    }else{
                        $('#flagform').prepend("<div id=\"flagresponse\" style=\"display: none; right: 0pt; left: 0pt; bottom: 2em; border: 1px solid red; padding: 3px; background: none repeat scroll 0pt 0pt rgb(255, 170, 170);\"> Error while connecting to the mainframe! The mainframe returned an unexpected result.</div>");
                    }
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
                    if(!endgame)
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
        dataType: 'json',
        data: dataString,
        success: function(data) {
            if(data != null && data.action == "leaderboard"){
                $('ol.scorelist').remove();
                $('#scorecontainer').append(data.reply);
                setTimeout("updateLeaderboard()",30000);
            }
        }
    });
}

function updateAnnouncements(){
    var dataString = 'ajax=announcements';

    $.ajax({
        type: "POST",
        url: "index.php",
        dataType: 'json',
        data: dataString,
        success: function(data) {
            if(data != null && data.action == "announcements"){
                $('#announcementdisplay').html(data.reply);
                setACollapseHandlers();
                for ( var i=0, len=openAnnouncements.length; i<len; ++i ){
                  $('label[for="announcement_'+openAnnouncements[i]+ '"]').find('div').show();
                }
                setTimeout("updateAnnouncements()",60000);
            }
        }
    });
}

var seriesInfo;

function updatePlotData(){
    var dataString = 'ajax=plotdata';

    $.ajax({
        type: "POST",
        url: "index.php",
        dataType: 'json',
        data: dataString,
        success: function(data) {
            if(data != null && data.action == "plotdata"){
                seriesInfo = new Array();

                for(var i = 0; i< data.series.length; i++){
                    var serieInf = new Object();
                    serieInf.showLabel = true;
                    serieInf.label = data.series[i];
                    seriesInfo.push(serieInf);
                }
                
                $('#chart1').empty();
                plot1 = $.jqplot('chart1', data.plotdata, {
                    legend:{show:true},
                    axes:{
                      xaxis:{
                        renderer:$.jqplot.DateAxisRenderer,
                                showTicks: false,
                                min: data.starttime,
                        tickOptions:{
                          formatString:'%r'
                        }
                      },
                      yaxis:{
                        tickOptions:{
                          formatString:'%i'
                          }
                      }
                    },
                    highlighter: {
                      show: true,
                      sizeAdjust: 7.5,
                              formatString: '%s, %s pts'
                    },
                    cursor: {
                              zoom: true,
                              showTooltip:false
                    },
                    series: seriesInfo
                });
                setTimeout("updatePlotData()",5*60000);
            }
        }
    });
}

