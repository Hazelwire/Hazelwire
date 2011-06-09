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
                            if $num_errors >0 or isset($cbansuccess)}
				{literal}<script type="text/javascript">
				    	$(document).ready(function(){
				    	alert(window.outerHeight);
				    		window.resizeTo(window.outerWidth ,205+$('#msgs').height());

				    	$('#acform').animate({top:'2em'});

				    	});
				    </script>{/literal}
				    <div id="msgs" style="display: block; left: 0pt; right: 0pt;height:auto;">
					{if isset($errors)}{foreach from=$errors item=error}
					<div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); left: 0pt; right: 0pt;  padding: 3px; position: relative; border:solid 1px red;">{$error->getMessage()}</div>
					{/foreach}{/if}
					{if isset($cbansuccess) && $cbansuccess == "1"}
					<div style="background: none repeat scroll 0pt 0pt rgb(170, 255, 170); left: 0pt; right: 0pt;  padding: 3px; position: relative; border:solid 1px rgb(0, 255, 0)">Contestant banned!</div>
					{/if}
					</div>
				{/if}
				<div id="acform">
					<div class="header">
						<h1>Ban {$contestant->getTeamname()}</h1>
					</div>
					<form><input type="hidden" name="cid" value="{$contestant->getId()}" />
						for <input type="text" name="cbantime" title="#" class="defaultText" /> minutes.
						<div class="buttons">
							<div>
								<input type="button" id="okcban" value="OK" />
								<input type="button" id="cancel" value="Cancel" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
