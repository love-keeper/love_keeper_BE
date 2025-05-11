output "bastion_id" {
  description = "The ID of the bastion instance"
  value       = aws_instance.bastion.id
}

output "bastion_public_ip" {
  description = "The public IP of the bastion instance"
  value       = aws_instance.bastion.public_ip
}

output "bastion_dns" {
  description = "The public DNS of the bastion instance"
  value       = aws_instance.bastion.public_dns
}