<!DOCTYPE html>
<html>
    <head>
        <title>Game Administration | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Game Administration</h2>
        {if $num_errors >0}
        {foreach from=$errors item=error}
            <span style="color: red;font-size:1.3em;border: 1px solid red;display:block;">{$error->getMessage()}</span>
        {/foreach}
        {/if}
        
        <form method="POST" action="index.php" >
             If you press this button, the game will be stopped.
             <input name="next" id="next" type="Submit" value="Stop the game!" />
        </form>
        <p></p>
        {contestants_list}
    </body>
</html>
