<!DOCTYPE html>
<html>
    <head>
        <title>Wargame Configuration | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        {if $db_created == 1}
            A database was just created with the filename <strong>{$db_file_name}</strong>. This is relative to the root of this website (<strong>{$site_path}</strong>).
        {/if}
        <h2>Config dingen bloat</h2>
        {if $num_errors >0}
        <span style="color: red;font-size:1.3em;border: 1px solid red">{$config_input_error}</span>
        {/if}
        <form method="POST" action="index.php" enctype="multipart/form-data">
            <input type="hidden" name="configsubmit" value="true" />
            <div style="padding-bottom:0.2em;"> <label> Name </label> <input type="text" name="name" size="15"/> </div>
            <div style="padding-bottom:0.2em;"> <label> P2P interval (Min) </label> <input type="text" name="p2p_interval" size="4" value="10"/> </div>
            <div style="padding-bottom:0.2em;"> <label> S2P interval (Min) </label> <input type="text" name="s2p_interval" size="4" value="2" /> </div>
            <div style="padding-bottom:0.2em;"> <label> Server's IP </label> <input type="text" name="server_ip" size="10" /> </div>
            <div style="padding-bottom:0.2em;"> <label> Manifest file </label> <input type="file" name="manifest" /> </div>
            <br />
            Optional:<br/>
            <div style="padding-bottom:0.2em;"> <label> Points Decay Modifier: </label> <input type="text" name="points_decay_mod" size="5" value="0.25" /> </div>
            <div style="padding-bottom:0.2em;"> <label> Minimum Points per Score: </label> <input type="text" name="points_min" size="5" value="1" /> pts</div>
            <div style="padding-bottom:0.2em;"> <label> Points Penalty Modifier: </label> <input type="text" name="point_penalty_mod" size="5" value="0.5" /> </div>
            <div style="padding-bottom:0.2em;"> <label> Offline Penalty: </label> <input type="text" name="penalty_offline" size="5" value="5" /> pts/min offline</div>
            <div> <label><!-- pseudo label --> </label>
                <input type="Submit" value="Next" /> </div>
        </form>
    </body>
</html>
