<?php


function smarty_function_content($params, $template)
{
    /* @var $swa SjikkeWorksArchive */
  global $swa;
  return $swa->doAction();
}

?>
