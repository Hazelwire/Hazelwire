var oTable;
function trim(str) {
        str = str.replace(/^\s+/, '');
        for (var i = str.length - 1; i >= 0; i--) {
                if (/\S/.test(str.charAt(i))) {
                        str = str.substring(0, i + 1);
                        break;
                }
        }
        return str;
}

jQuery.fn.dataTableExt.oSort['date-euro-asc'] = function(a, b) {
        if (trim(a) != '') {
                var frDatea = trim(a).split(' ');
                var frTimea = frDatea[1].split(':');
                var frDatea2 = frDatea[0].split('/');
                var x = (frDatea2[2] + frDatea2[1] + frDatea2[0] + frTimea[0] + frTimea[1] + frTimea[2]) * 1;
        } else {
                var x = 10000000000000; // = l'an 1000 ...
        }

        if (trim(b) != '') {
                var frDateb = trim(b).split(' ');
                var frTimeb = frDateb[1].split(':');
                frDateb = frDateb[0].split('/');
                var y = (frDateb[2] + frDateb[1] + frDateb[0] + frTimeb[0] + frTimeb[1] + frTimeb[2]) * 1;
        } else {
                var y = 10000000000000;
        }
        var z = ((x < y) ? -1 : ((x > y) ? 1 : 0));
        return z;
};

jQuery.fn.dataTableExt.oSort['date-euro-desc'] = function(a, b) {
        if (trim(a) != '') {
                var frDatea = trim(a).split(' ');
                var frTimea = frDatea[1].split(':');
                var frDatea2 = frDatea[0].split('/');
                var x = (frDatea2[2] + frDatea2[1] + frDatea2[0] + frTimea[0] + frTimea[1] + frTimea[2]) * 1;
        } else {
                var x = 10000000000000;
        }

        if (trim(b) != '') {
                var frDateb = trim(b).split(' ');
                var frTimeb = frDateb[1].split(':');
                frDateb = frDateb[0].split('/');
                var y = (frDateb[2] + frDateb[1] + frDateb[0] + frTimeb[0] + frTimeb[1] + frTimeb[2]) * 1;
        } else {
                var y = 10000000000000;
        }
        var z = ((x < y) ? 1 : ((x > y) ? -1 : 0));
        return z;
};
function buildSanityTable(){
    $('#popup > h1').html("Sanity Check Failure Overview");
    $("#popupcontent").html('<table id="sanityOverview" class="display" style="border:0px;border-spacing:1px;"><thead><tr><th>Name</th><th>Timestamp</th><th>IP</th><th>Port</th><th>Service</th><th>Reporter</th></tr></thead><tbody></tbody><tfoot><tr><th>Name</th><th>Timestamp</th><th>IP</th><th>Port</th><th>Service</th><th>Reporter</th></tr></thead></table>');
    oTable = $('#sanityOverview').dataTable( {
                                "bProcessing": true,
                                "sAjaxSource": "index.php?aaction=getsanity",
                                "bDeferRender": true,
                                "aLengthMenu": [[50, 100, 250, 500, -1], [50, 100, 250, 500, "All"]],
                                "iDisplayLength": 50,
                                "sScrollY": "480px",
                                "aoColumns": [
                                        null,
                                        { "sType": "date-euro" },
                                        null,
                                        null,
                                        null,
                                        null
                                ]
                        } );
    $("#popupcontent").css({
        "width": "800px",
        "height": "600px",
        "overflow":"auto"
    });
    $('#sanityOverview_next').html('>');
    $('#sanityOverview_previous').html('<');
    centerPopup(0);
    loadPopup();


}

