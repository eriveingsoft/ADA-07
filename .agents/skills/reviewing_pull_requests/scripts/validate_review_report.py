import sys
import os

def validate_report(filepath):
    if not os.path.exists(filepath):
        print(f"Error: El archivo '{filepath}' no existe.")
        sys.exit(1)

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    required_sections = [
        "Review Context",
        "Requirements / Acceptance Criteria Reviewed",
        "Test Evidence",
        "MUST FIX",
        "SHOULD FIX",
        "OPTIONAL",
        "Final Review Summary",
        "Human decision required"
    ]

    missing = []
    for section in required_sections:
        if section not in content:
            missing.append(section)

    if missing:
        print("INVALID: Al reporte le faltan las siguientes secciones:")
        for m in missing:
            print(f"- {m}")
        sys.exit(1)

    print("VALID: PR readiness review structure is complete.")
    sys.exit(0)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: python validate_review_report.py <ruta_del_reporte>")
        sys.exit(1)

    validate_report(sys.argv[1])