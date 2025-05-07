output "bastion_id" {
  description = "ID of Bastion instance"
  value       = aws_instance.bastion.id
}

output "bastion_public_ip" {
  description = "Public IP of Bastion instance"
  value       = aws_instance.bastion.public_ip
}

# output "key_name" {
#   description = "Name of the key pair"
#   value       = aws_key_pair.bastion.key_name
# }
