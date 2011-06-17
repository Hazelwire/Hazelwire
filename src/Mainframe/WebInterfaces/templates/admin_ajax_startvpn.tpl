<div id="response" style="display:none; position: absolute; left: 25%; right: 25%;height:auto;">
{if $num_errors >0}
{foreach from=$errors item=error}
<div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); padding: 3px; border: 1px solid red; text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">{$error->getMessage()}</div>
{/foreach}
{/if}
{if isset($success)}
<div style="background: none repeat scroll 0pt 0pt rgb(170, 255, 170); padding: 3px; border: 1px solid rgb(0, 255, 0); text-align: center; position: relative; margin-left: auto; margin-right: auto; min-width: 30em; margin-bottom: 3px;">{$success}</div>
{/if}
</div>