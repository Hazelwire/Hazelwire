<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
	<head>
		<title>[Insert wargame name here] - powered by Hazelwire</title>
		<link rel="stylesheet" type="text/css" href="admin.css"></link>
		<script type="text/javascript" src="jquery-1.6.1.min.js"></script>
		<script type="text/javascript" src="buttons.js"></script>
		<script type="text/javascript" src="textfield.js"></script>
	</head>
	<body>
		<div id="container">
			<div class="content">
				<div id="acform">
					<div class="header">
						<h1>Edit announcement</h1>
					</div>
					<form>
						<div id="atitle">
							<input type="text" name="atitle" title="Announcement title" class="defaultText filldiv" />
						</div>
						<div id="abody">
							<textarea name="abody" title="Announcement body" class="defaultText filldiv"></textarea>
						</div>
						<div class="buttons">
							<div>
								<input type="button" id="ok" value="Save" />
								<input type="button" id="cancel" value="Cancel" />
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>
</html>
