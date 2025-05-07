resource "aws_instance" "bastion" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  # key_name               = aws_key_pair.bastion.key_name  # 키 페어 리소스 주석 처리로 인해 제거
  vpc_security_group_ids = [var.bastion_security_group_id]
  subnet_id              = var.public_subnet_id
  associate_public_ip_address = true

  tags = {
    Name        = "${var.environment}-bastion"
    Environment = var.environment
  }

  root_block_device {
    volume_size = 8
    volume_type = "gp3"
    encrypted   = true
  }
}

# 키 파일이 없어서 오류가 발생하여 일시적으로 주석 처리
# resource "aws_key_pair" "bastion" {
#   key_name   = "${var.environment}-bastion-key"
#   public_key = file(var.public_key_path)
# }
