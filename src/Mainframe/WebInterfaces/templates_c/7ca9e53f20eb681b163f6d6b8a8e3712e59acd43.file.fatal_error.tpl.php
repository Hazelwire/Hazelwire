<?php /* Smarty version Smarty-3.0.6, created on 2011-04-28 15:58:38
         compiled from "D:\wampserver64\www\WebInterfaces\templates/fatal_error.tpl" */ ?>
<?php /*%%SmartyHeaderCode:98134db98eae44aaa8-03986388%%*/if(!defined('SMARTY_DIR')) exit('no direct access allowed');
$_smarty_tpl->decodeProperties(array (
  'file_dependency' => 
  array (
    '7ca9e53f20eb681b163f6d6b8a8e3712e59acd43' => 
    array (
      0 => 'D:\\wampserver64\\www\\WebInterfaces\\templates/fatal_error.tpl',
      1 => 1304002411,
      2 => 'file',
    ),
  ),
  'nocache_hash' => '98134db98eae44aaa8-03986388',
  'function' => 
  array (
  ),
  'has_nocache_code' => false,
)); /*/%%SmartyHeaderCode%%*/?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <title>A fatal error occured! | Hazelwire</title>

    </head>

    <body>
        <?php echo $_smarty_tpl->getVariable('errors')->value[0]->getMessage();?>

    </body>
</html>