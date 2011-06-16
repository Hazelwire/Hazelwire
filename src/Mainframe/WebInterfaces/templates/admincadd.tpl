<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>{$title} - powered by Hazelwire</title>
		<link rel="stylesheet" type="text/css" href="css/admin.css"></link>
		<script type="text/javascript" src="js/jquery-1.6.1.min.js"></script>
		<script type="text/javascript" src="js/buttons.js"></script>
		<script type="text/javascript" src="js/textfield.js"></script>
	</head>
	<body>

		<div id="container">
			<div class="content">
				{if $num_errors >0 or isset($caddsuccess)}
				{literal}<script type="text/javascript">
				    	$(document).ready(function(){
				    	
				    		window.resizeTo(window.outerWidth ,230+$('#msgs').height());
                                                $('#msgs').hide(1).delay(400).fadeIn();
                                                $('#acform').animate({top:'2em'});

				    	});
				    </script>{/literal}
				    <div id="msgs" style="display: block; left: 0pt; right: 0pt;height:auto;">
					{if isset($errors)}{foreach from=$errors item=error}
					<div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); left: 0pt; right: 0pt;  padding: 3px; position: relative; border:solid 1px red;">{$error->getMessage()}</div>
					{/foreach}{/if}
					{if isset($caddsuccess) && $caddsuccess == "1"}
					<div style="background: none repeat scroll 0pt 0pt rgb(170, 255, 170); left: 0pt; right: 0pt;  padding: 3px; position: relative; border:solid 1px rgb(0, 255, 0)">Contestant added!</div>
					{/if}
					</div>
				{/if}
				<div id="acform">
					<div class="header">
						<h1>Add contestant</h1>
					</div>
					<form method="POST"><input type="hidden" name="cadd" value="add" style="display:none;" />
						<div class="cforminput">
							<div class="cformlabel">Team name: </div>
							<div class="cformfield">
								<input type="text" name="cname" title="teamname" class="defaultText" />
							</div>
							<div class="cformlabel">Subnet:</div>
							<div class="cformfield">
								10.<input id="subnet" type="text" name="csubnet" value="1" title="#" class="defaultText" />.0.0/24
							</div>
							<div class="cformlabel">VM IP:</div>
							<div class="cformfield">
								10.<span id="subinvmip">1</span>.<input type="text" name="cvmip" value="1" title="#" class="defaultText" />.0
							</div>
						</div>
						<div class="buttons">
							<div>
								<input name="caddbutton" type="button" id="caddok" value="Add" />
								<input type="button" id="cancel" value="Cancel" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>