<?php
/**
 * Error is a class which is used by WebInterface in order to work with errors which have occurred.
 * An error is given a type, a message and whether the error which has occurred was fatal.
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

    /**
     * Returns the type of this Error which was set when creating it
     * @return string The type of Error
     */
    public function getType(){
        return $this->type;
    }

    /**
     * Returns whether this Error is a fatal one, i.e. execution should be halted ASAP
     * @return boolean true if fatal, false otherwise
     */
    public function isFatal(){
        return $this->fatal;
    }
    
}

?>
