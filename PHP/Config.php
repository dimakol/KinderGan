<?php

require_once 'cloudinary/Cloudinary.php';

/**
 * Database config variables
 */
define("DB_HOST", "***");
define("DB_USER", "***");
define("DB_PASSWORD", "***");
define("DB_DATABASE", "***");

/**
 * Cloudinary config variables
 */
 \Cloudinary::config(array(
    "cloud_name" => "***",
    "api_key" => "***",
    "api_secret" => "***"
));

?>