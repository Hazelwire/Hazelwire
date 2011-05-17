<!DOCTYPE html>
<html>
    <head>
        <title>Add Contestants | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Add Contestant Team</h2>
        {if $num_errors >0}
        <span style="color: red;font-size:1.3em;border: 1px solid red">{$team_input_error}</span>
        {/if}
        <form method="POST" action="index.php" enctype="multipart/form-data">
            <div style="padding-bottom:0.2em;"> <label> Name </label> <input type="text" name="name" size="15"/> </div>
            <div style="padding-bottom:0.2em;"> <label> IP Range </label> 10.<input type="text" name="ip_range" size="1" value="x"/>.0.0/24 </div>
            <div style="padding-bottom:0.2em;"> <label> Server IP </label> 10.x.<input type="text" name="server_ip" size="1" value="y" />.1 </div>
            <div> <label><!-- pseudo label --> </label>
                <input type="Submit" value="Add" name="button" />  <input name="button" type="Submit" value="Next" /></div>
        </form>
        <p></p>
        Contestants added so far: <br/><br/>
        <table>
            <tr>
                <td>ID</td><td>Name</td><td>Subnet</td><td>VMip</td><td>Links</td>
            </tr>
        {foreach from=$contestants item=contestant}
        <tr>
            <td>{$contestant->getId()}</td>
            <td>{$contestant->getTeamname()}</td>
            <td>{$contestant->getSubnet()}</td>
            <td>{$contestant->getVm_ip()}</td>
            <td><a href="download.php?type=tkey&team={$contestant->getTeamname()}">Team Key</a><br/>
                <a href="download.php?type=tcert&team={$contestant->getTeamname()}">Team Cert</a> <br/>
                <a href="download.php?type=vkey&team={$contestant->getTeamname()}">VM Key</a><br/>
                <a href="download.php?type=vcert&team={$contestant->getTeamname()}">VM Cert</a><br/>
                <a href="download.php?type=vpnconf&team={$contestant->getTeamname()}">VPN conf</a><br/>
                <a href="download.php?type=ca">CA</a></td>
        </tr>
        {/foreach}
        </table>
    </body>
</html>
