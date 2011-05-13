<!DOCTYPE html>
<html>
    <head>
        <title>VPN management | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Contestants' VPN management:</h2>
        {if $num_errors >0}
        <span style="color: red;font-size:1.3em;border: 1px solid red">{$vpn_error}</span>
        {/if}
        <form method="POST" action="index.php" >
            
            <table>
            <tr>
                <td>Name</td><td>Subnet</td><td>VMip</td><td>Links</td><td>Start/Stop</td>
            </tr>
            {foreach from=$contestants item=contestant}
            <tr>
                <td>{$contestant->getTeamname()}</td>
                <td>{$contestant->getSubnet()}</td>
                <td>{$contestant->getVm_ip()}</td>
                <td><a href="download.php?type=tkey&team={$contestant->getTeamname()}">Team Key</a><br/>
                    <a href="download.php?type=tcert&team={$contestant->getTeamname()}">Team Cert</a> <br/>
                    <a href="download.php?type=vkey&team={$contestant->getTeamname()}">VM Key</a><br/>
                    <a href="download.php?type=vcert&team={$contestant->getTeamname()}">VM Cert</a><br/>
                    <a href="download.php?type=ca">CA</a></td>
                <td>{if $contestant->getVPNStatus()} <input type="Submit" value="Stop" name="stop[{$contestant->getId()}]"  /> 
                    {else}<input type="Submit" value="Start" name="start[{$contestant->getId()}]" />{/if}
                </td>
            </tr>
            {/foreach}
        </table>
            <input type="Submit" value="Stop all" name="stopall" id="stopall" />  <input name="startall" id="startall" type="Submit" value="Start all" /> <input name="next" id="next" type="Submit" value="Next" />
        </form>
        <p></p>
       
    </body>
</html>
