<ul class="collapsible">
        {foreach from=$announcements item=announcement}
        <li>
                <input type="radio" name="announcement" id="announcement_{$announcement->id}"/>
                <label for="announcement_{$announcement->id}">{$announcement->title}
                        <span style="float: right; display: block; font-size: 0.8em; padding-right: 0.3em; padding-top: 2px;">{$announcement->timestamp|date_format:'%b %e, %Y @ %H:%M:%S'}</span>
                        <div>{$announcement->content}</div>
                </label>
        </li>
        {/foreach}
</ul>