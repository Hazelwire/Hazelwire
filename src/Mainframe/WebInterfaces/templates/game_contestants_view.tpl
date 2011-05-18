<!DOCTYPE html>
<html>
    <head>
        <title>Wargame (title to be added) | Hazelwire</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        <h2>Submit flag:</h2>
        {if $num_errors >0}
        {foreach from=$errors item=error}
            <span style="color: red;font-size:1.3em;border: 1px solid red;display:block;">{$error->getMessage()}</span>
        {/foreach}
        {/if}
        {if isset($flag_sucess) && $flag_success >0}
        <span style="color: green;font-size:1.3em;border: 1px solid green;display:block;">Flag successfully submited!</span>
        {/if}
        <form method="POST" action="index.php" >
            <div style="padding-bottom:0.2em;"> <label> Flag </label> <input type="text" name="flag" size="32"/> </div>
            <input type="Submit" value="Submit flag!" name="sub_flag"/> 
        </form>
        
        <p></p>
        <h2>Contestant overview:</h2>
        {contestants_list}
    </body>
</html>
