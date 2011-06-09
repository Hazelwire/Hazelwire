<div id="flagresponse" style="display: none; bottom: 2em; left: 0pt; right: 0pt;height:auto;">
{if $num_errors >0}
{foreach from=$errors item=error}
<div style="background: none repeat scroll 0pt 0pt rgb(255, 170, 170); left: 0pt; right: 0pt;  padding: 3px; position: relative; border:solid 1px red;">{$error->getMessage()}</div>
{/foreach}
{/if}
{if isset($flag_success) && $flag_success >0}
<div style="background: none repeat scroll 0pt 0pt rgb(170, 255, 170); left: 0pt; right: 0pt;  padding: 3px; position: relative; border:solid 1px rgb(0, 255, 0)">Flag successfully submited!</div>
{/if}
</div>