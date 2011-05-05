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
            <div style="padding-bottom:0.2em;"> <label> Name </label> <input type="text" name="name" size="15"/> </div>
            <div style="padding-bottom:0.2em;"> <label> P2P interval (Min) </label> <input type="text" name="p2p_interval" size="4" value="10"/> </div>
            <div style="padding-bottom:0.2em;"> <label> S2P interval (Min) </label> <input type="text" name="s2p_interval" size="4" value="2" /> </div>
            <div style="padding-bottom:0.2em;"> <label> Manifest file </label> <input type="file" name="manifest" /> </div>
            <div> <label><!-- pseudo label --> </label>
                <input type="Submit" value="Next" /> </div>
        </form>
    </body>
</html>
