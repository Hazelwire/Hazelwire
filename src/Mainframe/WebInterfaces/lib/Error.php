<?php
/**
 * Description of Error
 *
 * @author Daniel
 */
class Error extends Exception{
    private $type;
    private $fatal;
    
    /**
     *
     * @param string $type The error type.
     * @param string $message A description of the error.
     * @param bool $fatal If this error should stop normal page processing.
     */
    function __construct($type, $message, $fatal = false){
        parent::__construct($message);
        $this->type = $type;
        $this->fatal = $fatal;
    }
    
    public function getType(){
        return $this->type;
    }
    
    public function isFatal(){
        return $this->fatal;
    }
    
}

?>
