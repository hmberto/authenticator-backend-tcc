CREATE TABLE IF NOT EXISTS `users` (
	`user_id` int AUTO_INCREMENT PRIMARY KEY,
	`first_name` varchar(250),
	`last_name` varchar(250),
	`email` varchar(250) UNIQUE NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
$
CREATE TABLE IF NOT EXISTS `sessions` (
	`session_id` int AUTO_INCREMENT PRIMARY KEY,
	`user_id` int NOT NULL,
	`session` varchar(250) UNIQUE NOT NULL,
	`is_active` boolean NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id)
);
$
CREATE TABLE IF NOT EXISTS `otps` (
	`otp_id` int AUTO_INCREMENT PRIMARY KEY,
	`user_id` int NOT NULL,
	`otp` varchar(250) UNIQUE NOT NULL,
	`is_active` boolean NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id)
);
$
CREATE TABLE IF NOT EXISTS `email_verifications` (
	`email_verification_id` int AUTO_INCREMENT PRIMARY KEY,
	`user_id` int NOT NULL,
	`is_confirmed` boolean NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id)
);
$
CREATE TABLE IF NOT EXISTS `access_confirmations` (
	`access_confirmations_id` int AUTO_INCREMENT PRIMARY KEY,
	`user_id` int NOT NULL,
	`token` varchar(250) UNIQUE NOT NULL,
	`request_ip` varchar(250) NOT NULL,
	`request_browser` varchar(250) NOT NULL,
	`request_operational_system` varchar(250) NOT NULL,
	`is_approved` boolean NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id)
);