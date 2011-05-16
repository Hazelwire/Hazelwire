<!DOCTYPE html>
<html>
    <head>
        <title>Wargame not playable | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Wargame not playable</h2>
        {if $num_errors >0}
        {foreach from=$errors item=error}
            <span style="color: red;font-size:1.3em;border: 1px solid red;display:block;">{$error->getMessage()}</span>
        {/foreach}
        {/if}<br />
        Wargame is not playable yet or anymore. Too bad.
        <p></p>
       
    </body>
</html>
