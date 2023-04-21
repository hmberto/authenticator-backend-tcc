DROP TABLE IF EXISTS `email_verifications`;
$
DROP TABLE IF EXISTS `otps`;
$
DROP TABLE IF EXISTS `sessions`;
$
DROP TABLE IF EXISTS `users`;
$
CREATE TABLE `users` (
	`user_id` int AUTO_INCREMENT PRIMARY KEY,
	`first_name` varchar(250),
	`last_name` varchar(250),
	`email` varchar(250) UNIQUE NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
$
CREATE TABLE `sessions` (
	`session_id` int AUTO_INCREMENT PRIMARY KEY,
	`user_id` int NOT NULL,
	`session` varchar(250) UNIQUE NOT NULL,
	`is_active` boolean NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id)
);
$
CREATE TABLE `otps` (
	`otp_id` int AUTO_INCREMENT PRIMARY KEY,
	`user_id` int NOT NULL,
	`otp` varchar(250) UNIQUE NOT NULL,
	`is_active` boolean NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id)
);
$
CREATE TABLE `email_verifications` (
	`email_verification_id` int AUTO_INCREMENT PRIMARY KEY,
	`user_id` int NOT NULL,
	`is_confirmed` boolean NOT NULL,
	`created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
	`updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_user_id (user_id)
);