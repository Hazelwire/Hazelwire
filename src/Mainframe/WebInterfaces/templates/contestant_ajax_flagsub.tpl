<div id="flagresponse" style="bottom: 2em; left: 0pt; right: 0pt;">
{if $num_errors >0}
{foreach from=$errors item=error}
<div style="background: none repeat scroll 0pt 0pt rgb(255, 255, 170); left: 0pt; right: 0pt; margin: 2px; padding: 3px; position: relative;">{$error->getMessage()}</div>
{/foreach}
{/if}
{if isset($flag_success) && $flag_success >0}
<div style="background: none repeat scroll 0pt 0pt rgb(170, 255, 170); left: 0pt; right: 0pt; margin: 2px; padding: 3px; position: relative;">Flag successfully submited!</div>
{/if}
</div>