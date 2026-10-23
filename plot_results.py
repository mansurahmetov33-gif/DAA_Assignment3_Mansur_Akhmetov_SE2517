
from pathlib import Path
import pandas as pd
import matplotlib.pyplot as plt

BASE_DIR = Path(__file__).resolve().parent
CSV_PATH = BASE_DIR / "results" / "results.csv"
PLOTS_DIR = BASE_DIR / "results" / "plots"

PLOTS_DIR.mkdir(parents=True, exist_ok=True)

if not CSV_PATH.exists():
    raise FileNotFoundError(
        "results.csv not found. Run Benchmark.java first."
    )

df = pd.read_csv(CSV_PATH)

print(f"Loaded {len(df)} benchmark rows")

def create_plot(column, title, ylabel, filename):
    plt.figure(figsize=(10, 6))

    for algorithm, group in df.groupby("algorithm"):
        group = group.sort_values("m")
        plt.plot(
            group["m"],
            group[column],
            marker="o",
            label=algorithm
        )

    plt.title(title)
    plt.xlabel("Number of edges (m)")
    plt.ylabel(ylabel)
    plt.xscale("log")
    plt.grid(True, alpha=0.3)
    plt.legend()
    plt.tight_layout()

    output = PLOTS_DIR / filename
    plt.savefig(output, dpi=300)
    print(f"Saved: {output}")
    plt.close()

create_plot(
    "time_ms",
    "Execution Time vs Number of Edges",
    "Time (ms)",
    "execution_time.png"
)

create_plot(
    "edge_scans",
    "Edge Scans vs Number of Edges",
    "Edge Scans",
    "edge_scans.png"
)

kruskal = df[
    df["algorithm"].str.lower() == "kruskal"
    ].sort_values("m")

plt.figure(figsize=(10, 6))
plt.plot(
    kruskal["m"],
    kruskal["dsu_ops"],
    marker="o",
    label="Kruskal"
)
plt.title("DSU Operations vs Number of Edges")
plt.xlabel("Number of edges (m)")
plt.ylabel("DSU Operations")
plt.xscale("log")
plt.grid(True, alpha=0.3)
plt.legend()
plt.tight_layout()

output = PLOTS_DIR / "dsu_operations.png"
plt.savefig(output, dpi=300)
print(f"Saved: {output}")
plt.close()

print("All 3 PNG plots generated successfully!")
