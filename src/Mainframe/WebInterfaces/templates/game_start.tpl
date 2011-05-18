<!DOCTYPE html>
<html>
    <head>
        <title>Game Start | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Game Start</h2>
        {if $num_errors >0}
        {foreach from=$errors item=error}
            <span style="color: red;font-size:1.3em;border: 1px solid red;display:block;">{$error->getMessage()}</span>
        {/foreach}
        {/if}
        <form method="POST" action="index.php" >
            
           If you press this button, the game will start. Be prepared.
             <input name="next" id="next" type="Submit" value="Start the game!" />
        </form>
        <p></p>
       
    </body>
</html>
