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
                {if $num_errors >0}
                    <span style="color: red;font-size:1.3em;border: 1px solid red">{$team_input_error}</span>
                {/if}
		<div id="container">
			<div class="content">
				<div id="acform">
					<div class="header">
						<h1>Add contestant</h1>
					</div>
					<form method="POST">
						<div class="cforminput">
							<div class="cformlabel">Team name: </div>
							<div class="cformfield">
								<input type="text" name="cname" title="teamname" class="defaultText" />
							</div>
							<div class="cformlabel">Subnet:</div>
							<div class="cformfield">
								10.<input type="text" name="csubnet" value="1" title="#" class="defaultText" />.0.0/24
							</div>
							<div class="cformlabel">VM IP:</div>
							<div class="cformfield">
								10.1.<input type="text" name="cvmip" value="1" title="#" class="defaultText" />.0
							</div>
						</div>
						<div class="buttons">
							<div>
								<input name="caddbutton" type="button" id="ok" value="Add" />
								<input type="button" id="cancel" value="Cancel" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
