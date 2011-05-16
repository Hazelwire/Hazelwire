<!DOCTYPE html>
<html>
    <head>
        <title>Post Game | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Game End</h2>
        {if $num_errors >0}
        {foreach from=$errors item=error}
            <span style="color: red;font-size:1.3em;border: 1px solid red;display:block;">{$error->getMessage()}</span>
        {/foreach}
        {/if}
        Nothing to do here ... xD Perhaps a restart? Or smt else?
        <p></p>
       
    </body>
</html>
