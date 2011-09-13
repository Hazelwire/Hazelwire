
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>BANNED! - powered by Hazelwire</title>
		<link rel="stylesheet" type="text/css" href="css/admin.css"></link>
                <link rel="stylesheet" type="text/css" href="css/sanityTable.css"></link>
                <link rel="stylesheet" type="text/css" href="css/jquery.countdown.css"></link>
		<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
                <script type="text/javascript" src="js/jquery.dataTables.min.js"></script>
                <script type="text/javascript" src="js/jquery.countdown.min.js"></script>

		<script type="text/javascript" src="js/buttons.js"></script>
		<script type="text/javascript" src="js/collapse.js"></script>
                <script type="text/javascript" src="js/textfield.js"></script>
                <script type="text/javascript" src="js/admin.js"></script>
                <script type="text/javascript">
		{literal}$(function () {
				var austDay = new Date();
				austDay = new Date((Date.now() + {/literal}{math equation='x * y' x=$ban_time y=1000}{literal}));
				$('#countdown').countdown({until: austDay});
			});{/literal}
		</script>
                
	</head>

	<body>
		
		<div id="container">
                    <div style="right:0;left:0;top:0;bottom:0; text-align:center;padding-top:3em;font-size:3em;"> You are <span style="color:#cc0000">BANNED</span>!
                        <div id="countdown" style=" width:400px;left:50%;margin-left:-200px;font-size:0.5em;margin-top:1em;">Time until unban</div>
                    </div>
			
		</div>
                
	</body>
</html>

